package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.DuplicateApplicationForTransactionException;
import uk.gov.companieshouse.acsp.exception.InvalidTransactionStatusException;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.mapper.ACSPRegDataDtoDaoMapper;
import uk.gov.companieshouse.acsp.models.dao.Acsp;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dao.AcspDataSubmissionDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;
import uk.gov.companieshouse.acsp.util.ApiLogger;
import uk.gov.companieshouse.acsp.util.TransactionUtils;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.net.URI;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;


import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.LINK_SELF;
import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_ACSP;
import static uk.gov.companieshouse.acsp.util.Constants.COSTS_URI_SUFFIX;
import static uk.gov.companieshouse.acsp.util.Constants.RESUME_JOURNEY_URI_PATTERN;
import static uk.gov.companieshouse.acsp.util.Constants.SUBMISSION_URI_PATTERN;

@Service
public class AcspService {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    AcspRepository acspRepository;
    private final TransactionService transactionService;
    private final ACSPRegDataDtoDaoMapper acspRegDataDtoDaoMapper;
    private final TransactionUtils transactionUtils;

    @Autowired
    public AcspService(AcspRepository acspRepository,
                       TransactionService transactionService,
                       ACSPRegDataDtoDaoMapper acspRegDataDtoDaoMapper,
                       TransactionUtils transactionUtils) {
        this.acspRepository = acspRepository;
        this.transactionService = transactionService;
        this.acspRegDataDtoDaoMapper = acspRegDataDtoDaoMapper;
        this.transactionUtils = transactionUtils;
    }

    public ResponseEntity<Object> createAcspRegData(Transaction transaction, AcspDataDto acspData,
                                                  String requestId) {
        return createApplicationAndUpdateTransaction(transaction, acspData, requestId);
    }

    private String autoGenerateId() {
        var random = new SecureRandom();
        var values = new byte[4];
        random.nextBytes(values);
        var rand = String.format("%010d", random.nextInt(Integer.MAX_VALUE));
        var time = String.format("%08d", Calendar.getInstance().getTimeInMillis() / 100000L);
        var rawId = rand + time;
        var acspId = rawId.split("(?<=\\G.{6})");
        return String.join("-", acspId);
    }
    private ResponseEntity<Object> createApplicationAndUpdateTransaction(Transaction transaction,
                                                                         AcspDataDto acspDataDto,
                                                                         String requestId) {

        var acspDataDao = acspRegDataDtoDaoMapper.dtoToDao(acspDataDto);
        acspDataDao.setId(autoGenerateId());
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        try {
            if (transaction.getResources() != null && !transaction.getResources().isEmpty()) {
                throw new DuplicateApplicationForTransactionException("An application already exists for this transaction " + transaction.getId());
            }
            var insertedSubmission = acspRepository.insert(acsp);

            String submissionId = insertedSubmission.getId();
            final String submissionUri = getSubmissionUri(transaction.getId(), submissionId);
            updateAcspRegWithMetaData(insertedSubmission.getAcspDataDao(), transaction, submissionUri, requestId);
            acspRepository.save(insertedSubmission);
            // create the Resource to be added to the Transaction (includes various links to the resource)
            var acspTransactionResource = createAcspTransactionResource(submissionUri);

            updateTransactionWithLinks(transaction, submissionId, submissionUri, acspTransactionResource, requestId);
            ApiLogger.infoContext(requestId, String.format("ACSP Submission created for transaction id: %s with acsp submission id: %s",
                    transaction.getId(), insertedSubmission.getId()));

            acspDataDto = acspRegDataDtoDaoMapper.daoToDto(insertedSubmission.getAcspDataDao());

            return ResponseEntity.created(URI.create(submissionUri)).body(acspDataDto);
        } catch (DuplicateKeyException e) {
            LOGGER.error("A document already exist with this id " + acspDataDao.getId());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (DuplicateApplicationForTransactionException e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            LOGGER.error("An error occurred for transaction " + transaction.getId() + ", " + e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public ResponseEntity<Object> updateACSPDetails(Transaction transaction,
                                                                AcspDataDto acspDataDto,
                                                                String requestId,
                                                                String acspId) throws SubmissionNotLinkedToTransactionException, InvalidTransactionStatusException, ServiceException {
        if (!transactionUtils.isTransactionLinkedToAcspSubmission(transaction, acspDataDto)) {
            throw new SubmissionNotLinkedToTransactionException(String.format(
                    "Transaction id: %s does not have a resource that matches acsp id: %s", transaction.getId(), acspId));
        }
        //check for browser back etc.
        if (TransactionStatus.CLOSED == transaction.getStatus()
                || TransactionStatus.DELETED == transaction.getStatus()){
            throw new InvalidTransactionStatusException(String.format(
                    "Can't update transaction with stastus: %s ", transaction.getStatus().toString()));
        }

        if (acspDataDto.getCompanyDetails() != null && acspDataDto.getCompanyDetails().getCompanyName() != null && acspDataDto.getCompanyDetails().getCompanyNumber() != null) {
            if (!acspDataDto.getCompanyDetails().getCompanyName().equals(transaction.getCompanyName()) ||
                !acspDataDto.getCompanyDetails().getCompanyNumber().equals(transaction.getCompanyNumber())) {
                transaction.setCompanyName(acspDataDto.getCompanyDetails().getCompanyName());
                transaction.setCompanyNumber(acspDataDto.getCompanyDetails().getCompanyNumber());
                transactionService.updateTransaction(requestId, transaction);
            }
        } else if (acspDataDto.getBusinessName() != null && !acspDataDto.getBusinessName().equals(transaction.getCompanyName())) {
                transaction.setCompanyName(acspDataDto.getBusinessName());
                transaction.setCompanyNumber(null);
                transactionService.updateTransaction(requestId, transaction);
        } else {
            LOGGER.debug("No company details found in acspDataDto");
        }
        var acspDataDao = acspRegDataDtoDaoMapper.dtoToDao(acspDataDto);
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());

        var updatedSubmission = acspRepository.save(acsp);
        ApiLogger.infoContext(requestId, String.format("ACSP Submission created for transaction id: %s with acsp submission id: %s",
                transaction.getId(), updatedSubmission.getId()));
        acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspDataDao);
        return ResponseEntity.ok().body(acspDataDto);
    }


    private void updateAcspRegWithMetaData(AcspDataDao acspData,
                                           Transaction transaction,
                                           String submissionUri,
                                           String requestId) {
        var submission = new AcspDataSubmissionDao();
        submission.setCreatedAt(LocalDateTime.now());
        submission.setHttpRequestId(requestId);
        submission.setLastModifiedByUserId(transaction.getCreatedBy().get("id"));
        acspData.setAcspDataSubmission(submission);
        acspData.setLinks(Collections.singletonMap(LINK_SELF, submissionUri));
    }

    public Optional<AcspDataDto> getAcsp(String acspId, Transaction transaction) throws SubmissionNotLinkedToTransactionException {

        Optional<Acsp> acspData = acspRepository.findById(acspId);
        if (acspData.isPresent()) {
            var acspDataDao = acspData.get().getAcspDataDao();
            var acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspDataDao);
            if (!transactionUtils.isTransactionLinkedToAcspSubmission(transaction, acspDataDto)) {
                throw new SubmissionNotLinkedToTransactionException(String.format(
                        "Transaction id: %s does not have a resource that matches acsp id: %s", transaction.getId(), acspId));
            }

            return Optional.of(acspDataDto);
        } else {
            return Optional.empty();
        }
    }

    public ResponseEntity<Object> getAcspApplicationStatus(String userId, String requestId){
        try{
            var application = acspRepository.findById(userId);
            if (application.isEmpty()){
                LOGGER.info("No application found for userId: " + userId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String transactionId = application.get().getAcspDataDao().getLinks().get("self").split("/")[2];
            var transaction = transactionService.getTransaction(requestId, transactionId);
            if(!TransactionStatus.CLOSED.equals(transaction.getStatus())) {
                LOGGER.info("Open application found for userId: " + userId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            if(transaction.getFilings().get(transactionId + "-1").getStatus().equals("rejected")){
                LOGGER.info("Rejected application found for userId: " + userId);
                acspRepository.delete(application.get());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.info("Application for " + userId + " is closed and not rejected");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Resource createAcspTransactionResource(String submissionUri) {
        var acspResource = new Resource();
        acspResource.setKind(FILING_KIND_ACSP);

        Map<String, String> linksMap = new HashMap<>();
        linksMap.put("resource", submissionUri);
        linksMap.put("costs", submissionUri + COSTS_URI_SUFFIX);

        acspResource.setLinks(linksMap);
        return acspResource;
    }

    private void updateTransactionWithLinks(Transaction transaction,
                                            String submissionId,
                                            String submissionUri,
                                            Resource resource,
                                            String requestId) throws ServiceException {
        transaction.setResources(Collections.singletonMap(submissionUri, resource));
        var resumeJourneyUri = String.format(RESUME_JOURNEY_URI_PATTERN, transaction.getId(), submissionId);
        transaction.setResumeJourneyUri(resumeJourneyUri);
        transactionService.updateTransaction(requestId,transaction);
    }

    private String getSubmissionUri(String transactionId, String submissionId) {
        return String.format(SUBMISSION_URI_PATTERN, transactionId, submissionId);
    }

    public ResponseEntity<Object> deleteAcspApplication(String id) {
        try {
            acspRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOGGER.error("Error deleting document with id " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> deleteAcspApplicationAndTransaction(String passThroughHeader, String id, String transactionId) {
        try {
            acspRepository.deleteById(id);
            transactionService.deleteTransaction(passThroughHeader, transactionId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOGGER.error("Error deleting document with id " + id + " and transaction ID: " + transactionId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
