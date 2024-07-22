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
import uk.gov.companieshouse.acsp.exception.ServiceException;
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
        LOGGER.info("Received POST request to save ACSP data.");
        LOGGER.debug("Request details: Transaction ID: " + transactionId +
                ", Request ID: " + requestId +
                ", User ID: " + userId +
                ", Transaction: " + transaction +
                ", ACSP Data: " + acspData);
        try {
            return acspService.createAcspRegData(transaction, acspData, requestId, userId);
        } catch (Exception e) {
            LOGGER.error("Error creating ACSP record. Exception: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/acsp")
    public ResponseEntity<Object> saveAcspData(
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId,
            @RequestHeader(value = ERIC_IDENTITY) String userId,
            @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction,
            @RequestBody AcspDataDto acspData) throws ServiceException {
        LOGGER.info("Received PUT request to update ACSP data.");
        LOGGER.debug("Request details: Transaction ID: " + transactionId +
                ", Request ID: " + requestId +
                ", User ID: " + userId +
                ", Transaction: " + transaction +
                ", ACSP Data: " + acspData);
        try {
            return acspService.updateACSPDetails(transaction, acspData, requestId, userId);
        } catch (SubmissionNotLinkedToTransactionException | InvalidTransactionStatusException e) {
            LOGGER.error("Error updating ACSP record. Exception: " + e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/acsp/{acsp_id}")
    public ResponseEntity<Object> getAcspData(@PathVariable(TRANSACTION_ID_KEY) String transactionId,
                                              @PathVariable("acsp_id") String acspId,
                                              @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction,
                                              @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId) {
        LOGGER.info("Received GET request to retrieve ACSP data.");
        LOGGER.debug("Request details: Transaction ID: " + transactionId +
                ", ACSP ID: " + acspId +
                ", Request ID: " + requestId +
                ", Transaction: " + transaction);
        Optional<AcspDataDto> acspData;
        try {
            acspData = acspService.getAcsp(acspId, transaction);
        } catch (SubmissionNotLinkedToTransactionException e) {
            LOGGER.error("Error retrieving ACSP data. Exception: " + e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        if (acspData.isEmpty()) {
            LOGGER.info("No ACSP data found for ACSP ID: " + acspId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            LOGGER.debug("Found ACSP data: " + acspData);
            return ResponseEntity.ok().body(acspData);
        }
    }

    @GetMapping("/acsp-api/user/{acsp_id}/application")
    public ResponseEntity<Object> checkHasApplication(@PathVariable("acsp_id") String acspId,
                                                      @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId) {
        LOGGER.info("Received GET request to check for user applications.");
        LOGGER.debug("Request details: ACSP ID: " + acspId +
                ", Request ID: " + requestId);
        return acspService.getAcspApplicationStatus(acspId, requestId);
    }

    @DeleteMapping("/acsp-api/user/{acsp_id}/application")
    public ResponseEntity<Object> deleteApplication(@PathVariable("acsp_id") String acspId) {
        LOGGER.info("Received DELETE request to delete application for ID: " + acspId);
        try {
            return acspService.deleteAcspApplication(acspId);
        } catch (Exception e) {
            LOGGER.error("Error deleting application. Exception: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
