package uk.gov.companieshouse.acsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.companieshouse.acsp.exception.InvalidTransactionStatusException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.service.AcspService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Optional;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.TRANSACTION_ID_KEY;
import static uk.gov.companieshouse.acsp.util.Constants.ERIC_ACCESS_TOKEN;
import static uk.gov.companieshouse.acsp.util.Constants.ERIC_IDENTITY;
import static uk.gov.companieshouse.acsp.util.Constants.TRANSACTION_KEY;


@RestController
public class AcspController {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    @Autowired
    private AcspService acspService;

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/acsp")
    public ResponseEntity<Object> createAcspData(
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId,
            @RequestHeader(value = ERIC_IDENTITY) String userId,
            @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction,
            @RequestBody AcspDataDto acspData) {
        LOGGER.info("received POST request to save acsp data");
        try {
            return acspService.createAcspRegData(transaction, acspData, requestId, userId);
        } catch (Exception e) {
            LOGGER.error("Error creating record " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/acsp")
    public ResponseEntity<Object> saveAcspData(
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId,
            @RequestHeader(value = ERIC_IDENTITY) String userId,
            @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction,
            @RequestBody AcspDataDto acspData) {
        LOGGER.info("received request to PUT acsp data");
        try {
            return acspService.updateACSPDetails(transaction, acspData, requestId, userId);
        } catch (SubmissionNotLinkedToTransactionException | InvalidTransactionStatusException e) {
            LOGGER.error("Error updating record " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/acsp/{acsp_id}")
    public ResponseEntity<Object> getAcspData(@PathVariable(TRANSACTION_ID_KEY) String transactionId,
                                              @PathVariable("acsp_id") String acsp_id,
                                              @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction,
                                              @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId) {
        LOGGER.info("received request to get acsp data");
        Optional<AcspDataDto> acspData;
        try {
            acspData = acspService.getAcsp(acsp_id, transaction);
        } catch (SubmissionNotLinkedToTransactionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        if (acspData.isEmpty()){
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }else{
            return ResponseEntity.ok().body(acspData);
        }
    }

    @GetMapping("/acsp-api/user/{acsp_id}/application")
    public ResponseEntity<Object> checkHasApplication(@PathVariable("acsp_id") String acsp_id,
                                                @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId){
        LOGGER.info("received request to check for user applications");
        return acspService.getAcspApplicationCount(acsp_id);
    }

    @DeleteMapping("/acsp-api/user/{acsp_id}/application")
    public ResponseEntity<Object> deleteApplication(@PathVariable("acsp_id") String acsp_id) {
        LOGGER.info("received request to delete application for id: " + acsp_id);
        return acspService.deleteAcspApplication(acsp_id);
    }

}
