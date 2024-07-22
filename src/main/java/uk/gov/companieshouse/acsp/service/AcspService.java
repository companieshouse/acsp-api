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
        LOGGER.info("AcspService constructor called");
        this.acspRepository = acspRepository;
        this.transactionService = transactionService;
        this.acspRegDataDtoDaoMapper = acspRegDataDtoDaoMapper;
        this.transactionUtils = transactionUtils;
    }

    public ResponseEntity<Object> createAcspRegData(Transaction transaction, AcspDataDto acspData,
                                                    String requestId, String userId) {
        LOGGER.info("createAcspRegData called. Transaction ID: " + transaction.getId() + ", Request ID: " + requestId + ", User ID: " + userId);
        return createDataAndUpdateTransaction(transaction, acspData, requestId, userId);
    }

    private ResponseEntity<Object> createDataAndUpdateTransaction(Transaction transaction,
                                                                  AcspDataDto acspDataDto,
                                                                  String requestId,
                                                                  String userId) {
        LOGGER.info("createDataAndUpdateTransaction called. Transaction ID: " + transaction.getId() + ", Request ID: " + requestId + ", User ID: " + userId);
        var acspDataDao = acspRegDataDtoDaoMapper.dtoToDao(acspDataDto);
        LOGGER.debug("acspDataDto to acspDataDao mapping done: " + acspDataDto);
        String submissionId = acspDataDao.getId();
        LOGGER.debug("Generated submission ID: " + submissionId);
        final String submissionUri = getSubmissionUri(transaction.getId(), submissionId);
        LOGGER.debug("Generated submission URI: " + submissionUri);
        updateAcspRegWithMetaData(acspDataDao, submissionUri, requestId, userId);

        var acspTransactionResource = createAcspTransactionResource(submissionUri);
        try {
            LOGGER.info("Inserting ACSP data into repository. Submission ID: " + submissionId);
            var insertedSubmission = acspRepository.insert(acspDataDao);
            updateTransactionWithLinks(transaction, submissionId, submissionUri, acspTransactionResource, requestId);
            ApiLogger.infoContext(requestId, String.format("ACSP Submission created for transaction id: %s with acsp submission id: %s",
                    transaction.getId(), insertedSubmission.getId()));
            acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspDataDao);
            LOGGER.debug("acspDataDao to acspDataDto mapping done: " + acspDataDto);
            LOGGER.info("ACSP registration data successfully created. Submission URI: " + submissionUri);
            return ResponseEntity.created(URI.create(submissionUri)).body(acspDataDto);
        } catch (DuplicateKeyException e) {
            LOGGER.error("A document already exists with this ID: " + acspDataDao.getId(), e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            LOGGER.error("An error occurred for transaction ID: " + transaction.getId() + ", Request ID: " + requestId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateACSPDetails(Transaction transaction,
                                                    AcspDataDto acspDataDto,
                                                    String requestId,
                                                    String acspId) throws SubmissionNotLinkedToTransactionException, InvalidTransactionStatusException, ServiceException {
        LOGGER.info("updateACSPDetails called. Transaction ID: " + transaction.getId() + ", ACSP ID: " + acspId + ", Request ID: " + requestId);
        LOGGER.debug("acspDataDto received: " + acspDataDto);
        if (!transactionUtils.isTransactionLinkedToAcspSubmission(transaction, acspDataDto)) {
            String errorMessage = String.format("Transaction ID: %s does not have a resource that matches ACSP ID: %s",
                    transaction.getId(), acspId);
            LOGGER.error(errorMessage);
            throw new SubmissionNotLinkedToTransactionException(errorMessage);
        }
        if (TransactionStatus.CLOSED == transaction.getStatus() || TransactionStatus.DELETED == transaction.getStatus()) {
            String errorMessage = String.format("Cannot update transaction with status: %s", transaction.getStatus().toString());
            LOGGER.error(errorMessage);
            throw new InvalidTransactionStatusException(errorMessage);
        }

        if (acspDataDto.getCompanyDetails() != null) {
            LOGGER.debug("Updating company details for transaction. Company Name: " +
                    acspDataDto.getCompanyDetails().getCompanyName() + ", Company Number: " +
                    acspDataDto.getCompanyDetails().getCompanyNumber());
            transaction.setCompanyName(acspDataDto.getCompanyDetails().getCompanyName());
            transaction.setCompanyNumber(acspDataDto.getCompanyDetails().getCompanyNumber());

            transactionService.updateTransaction(requestId, transaction);
        } else {
            LOGGER.debug("No company details found in acspDataDto");
        }

        var acspDataDao = acspRegDataDtoDaoMapper.dtoToDao(acspDataDto);
        LOGGER.debug("acspDataDto to acspDataDao mapping done: " + acspDataDto);
        var updatedSubmission = acspRepository.save(acspDataDao);
        ApiLogger.infoContext(requestId, String.format("ACSP Submission updated for transaction ID: %s with ACSP submission ID: %s",
                transaction.getId(), updatedSubmission.getId()));
        acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspDataDao);
        LOGGER.debug("acspDataDao to acspDataDto mapping done: " + acspDataDto);
        LOGGER.info("ACSP details updated successfully. Transaction ID: " + transaction.getId() + ", ACSP ID: " + acspId);
        return ResponseEntity.ok().body(acspDataDto);
    }

    private void updateAcspRegWithMetaData(AcspDataDao acspData,
                                           String submissionUri,
                                           String requestId,
                                           String userId) {
        LOGGER.debug("updateAcspRegWithMetaData called. Submission URI: " + submissionUri + ", Request ID: " + requestId + ", User ID: " + userId);
        var submission = new AcspDataSubmissionDao();
        submission.setLinks(Collections.singletonMap(LINK_SELF, submissionUri));
        submission.setCreatedAt(LocalDateTime.now());
        submission.setHttpRequestId(requestId);
        submission.setLastModifiedByUserId(userId);
        acspData.setAcspDataSubmission(submission);
    }

    public Optional<AcspDataDto> getAcsp(String acspId, Transaction transaction) throws SubmissionNotLinkedToTransactionException {
        LOGGER.info("getAcsp called. ACSP ID: " + acspId + ", Transaction ID: " + transaction.getId());
        Optional<AcspDataDao> acspData = acspRepository.findById(acspId);
        if (acspData.isPresent()) {
            var acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspData.get());
            LOGGER.debug("acspDataDao to acspDataDto mapping done: " + acspDataDto);
            if (!transactionUtils.isTransactionLinkedToAcspSubmission(transaction, acspDataDto)) {
                String errorMessage = String.format("Transaction ID: %s does not have a resource that matches ACSP ID: %s",
                        transaction.getId(), acspId);
                LOGGER.error(errorMessage);
                throw new SubmissionNotLinkedToTransactionException(errorMessage);
            }
            return Optional.of(acspDataDto);
        } else {
            LOGGER.info("No ACSP data found for ACSP ID: " + acspId);
            return Optional.empty();
        }
    }

    public ResponseEntity<Object> getAcspApplicationStatus(String userId, String requestId) {
        LOGGER.info("getAcspApplicationStatus called. User ID: " + userId + ", Request ID: " + requestId);
        try {
            var application = acspRepository.findById(userId);
            if (application.isEmpty()) {
                LOGGER.info("No application found for User ID: " + userId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String transactionId = application.get().getAcspDataSubmission().getLinks().get("self").split("/")[2];
            var transaction = transactionService.getTransaction(requestId, transactionId);
            if (!TransactionStatus.CLOSED.equals(transaction.getStatus())) {
                LOGGER.info("Open application found for User ID: " + userId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            if (transaction.getFilings().get(transactionId + "-1").getStatus().equals("rejected")) {
                LOGGER.info("Rejected application found for User ID: " + userId);
                acspRepository.delete(application.get());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            LOGGER.info("Application for User ID: " + userId + " is closed and not rejected");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception exception) {
            LOGGER.error("Error occurred while getting ACSP application status for User ID: " + userId + ", Request ID: " + requestId, exception);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Resource createAcspTransactionResource(String submissionUri) {
        LOGGER.debug("Creating ACSP transaction resource. Submission URI: " + submissionUri);
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
        LOGGER.debug("Updating transaction with links. Transaction ID: " + transaction.getId() + ", Submission ID: " + submissionId + ", Submission URI: " + submissionUri);
        transaction.setResources(Collections.singletonMap(submissionUri, resource));
        var resumeJourneyUri = String.format(RESUME_JOURNEY_URI_PATTERN, transaction.getId(), submissionId);
        transaction.setResumeJourneyUri(resumeJourneyUri);
        transactionService.updateTransaction(requestId, transaction);
    }

    private String getSubmissionUri(String transactionId, String submissionId) {
        LOGGER.debug("Generating submission URI. Transaction ID: " + transactionId + ", Submission ID: " + submissionId);
        return String.format(SUBMISSION_URI_PATTERN, transactionId, submissionId);
    }

    public ResponseEntity<Object> deleteAcspApplication(String id) {
        LOGGER.info("Deleting ACSP application. ID: " + id);
        try {
            acspRepository.deleteById(id);
            LOGGER.info("ACSP application deleted successfully. ID: " + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOGGER.error("Error deleting document with ID: " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
