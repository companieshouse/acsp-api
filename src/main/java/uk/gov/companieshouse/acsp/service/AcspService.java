package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.InvalidTransactionStatusException;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.mapper.ACSPRegDataDtoDaoMapper;
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
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.*;

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
        return createDataAndUpdateTransaction(transaction, acspData, requestId);
    }

    private ResponseEntity<Object> createDataAndUpdateTransaction(Transaction transaction,
                                                                AcspDataDto acspDataDto,
                                                                String requestId) {

        var acspDataDao = acspRegDataDtoDaoMapper.dtoToDao(acspDataDto);
        String submissionId = acspDataDao.getId();
        final String submissionUri = getSubmissionUri(transaction.getId(), submissionId);
        updateAcspRegWithMetaData(acspDataDao, submissionUri, requestId);

        // create the Resource to be added to the Transaction (includes various links to the resource)
        var acspTransactionResource = createAcspTransactionResource(submissionUri);
        try {
            var insertedSubmission = acspRepository.insert(acspDataDao);
            updateTransactionWithLinks(transaction, submissionId, submissionUri, acspTransactionResource, requestId);
            ApiLogger.infoContext(requestId, String.format("ACSP Submission created for transaction id: %s with acsp submission id: %s",
                    transaction.getId(), insertedSubmission.getId()));

            acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspDataDao);

            return ResponseEntity.created(URI.create(submissionUri)).body(acspDataDto);
        } catch (DuplicateKeyException e) {
            LOGGER.error("A document already exist with this id " + acspDataDao.getId());
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

        if (acspDataDto.getCompanyDetails() != null) {
            transaction.setCompanyName(acspDataDto.getCompanyDetails().getCompanyName());
            transaction.setCompanyNumber(acspDataDto.getCompanyDetails().getCompanyNumber());
            transactionService.updateTransaction(requestId, transaction);
        } else if (acspDataDto.getBusinessName() != null) {
            transaction.setCompanyName(acspDataDto.getBusinessName());
            transactionService.updateTransaction(requestId, transaction);
        } else {
            LOGGER.debug("No company details found in acspDataDto");
        }
        var acspDataDao = acspRegDataDtoDaoMapper.dtoToDao(acspDataDto);

        var updatedSubmission = acspRepository.save(acspDataDao);
        ApiLogger.infoContext(requestId, String.format("ACSP Submission created for transaction id: %s with acsp submission id: %s",
                transaction.getId(), updatedSubmission.getId()));
        acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspDataDao);
        return ResponseEntity.ok().body(acspDataDto);
    }


    private void updateAcspRegWithMetaData(AcspDataDao acspData,
                                           String submissionUri,
                                           String requestId) {
        var submission = new AcspDataSubmissionDao();
        submission.setCreatedAt(LocalDateTime.now());
        submission.setHttpRequestId(requestId);
        submission.setLastModifiedByUserId(acspData.getId());
        acspData.setAcspDataSubmission(submission);
        acspData.setLinks(Collections.singletonMap(LINK_SELF, submissionUri));
    }

    public Optional<AcspDataDto> getAcsp(String acspId, Transaction transaction) throws SubmissionNotLinkedToTransactionException {

        Optional<AcspDataDao> acspData = acspRepository.findById(acspId);
        if (acspData.isPresent()) {
            var acspDataDao = acspData.get();
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
            String transactionId = application.get().getLinks().get("self").split("/")[2];
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

    public ResponseEntity<Object> deleteAcspApplicationAndTransaction(String id, String transactionId) {
        try {
            acspRepository.deleteById(id);
            transactionService.deleteTransaction(transactionId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOGGER.error("Error deleting document with id " + id + "and transaction ID: " + transactionId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
