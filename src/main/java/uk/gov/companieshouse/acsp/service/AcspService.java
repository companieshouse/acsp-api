package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.mapper.ACSPRegDataDtoDaoMapper;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dao.AcspDataSubmissionDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;
import uk.gov.companieshouse.acsp.util.ApiLogger;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
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

    @Autowired
    public AcspService(AcspRepository acspRepository, TransactionService transactionService, ACSPRegDataDtoDaoMapper acspRegDataDtoDaoMapper) {
        this.acspRepository = acspRepository;
        this.transactionService = transactionService;
        this.acspRegDataDtoDaoMapper = acspRegDataDtoDaoMapper;
    }

    public ResponseEntity<Object> saveAcspRegData(Transaction transaction, AcspDataDto acspData,
                                                  String requestId, String userId) throws ServiceException {
        return saveDataAndUpdateTransaction(transaction, acspData, requestId, userId);
    }

    private ResponseEntity<Object> saveDataAndUpdateTransaction(Transaction transaction,
                                                                AcspDataDto acspDataDto,
                                                                String requestId,
                                                                String userId) throws ServiceException {

        var acspDataDao = acspRegDataDtoDaoMapper.dtoToDao(acspDataDto);
        String submissionId = acspDataDao.getId();
        final String submissionUri = getSubmissionUri(transaction.getId(), submissionId);
        updateAcspRegWithMetaData(acspDataDao, submissionUri, requestId, userId);

        // create the Resource to be added to the Transaction (includes various links to the resource)
        var acspTransactionResource = createAcspTransactionResource(submissionUri);
        var insertedSubmission = acspRepository.save(acspDataDao);
        updateTransactionWithLinks(transaction, submissionId, submissionUri, acspTransactionResource, requestId);
        ApiLogger.infoContext(requestId, String.format("ACSP Submission created for transaction id: %s with acsp submission id: %s",
                transaction.getId(), insertedSubmission.getId()));
        acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspDataDao);
        return ResponseEntity.created(URI.create(submissionUri)).body(acspDataDto);
    }


    private void updateAcspRegWithMetaData(AcspDataDao acspData,
                                           String submissionUri,
                                           String requestId,
                                           String userId) {
        var submission = new AcspDataSubmissionDao();
        submission.setLinks(Collections.singletonMap(LINK_SELF, submissionUri));
        submission.setCreatedAt(LocalDateTime.now());
        submission.setHttpRequestId(requestId);
        submission.setLastModifiedByUserId(userId);
        acspData.setAcspDataSubmission(submission);
    }



    public Optional<AcspDataDto> getAcsp(String id) {
        Optional<AcspDataDao> acspData = acspRepository.findById(id);
        if(acspData.isPresent()) {
            var acspDataDto = acspRegDataDtoDaoMapper.daoToDto(acspData.get());
            return Optional.of(acspDataDto);
        } else {
            return Optional.empty();
        }
    }

    public ResponseEntity<Object> getAcspApplicationCount(String userId){

        int acspCount = acspRepository.countById(userId);
        if (acspCount < 1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
        var resumeJourneyUri = String.format(RESUME_JOURNEY_URI_PATTERN, transaction.getId(), submissionId); //fix the RESUME_JOURNEY_URI_PATTERN
        transaction.setResumeJourneyUri(resumeJourneyUri);
        transactionService.updateTransaction(requestId,transaction);
    }

    private String getSubmissionUri(String transactionId, String submissionId) {
        return String.format(SUBMISSION_URI_PATTERN, transactionId, submissionId);
    }
}
