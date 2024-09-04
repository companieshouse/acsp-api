package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import uk.gov.companieshouse.acsp.exception.InvalidTransactionStatusException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.service.AcspService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.util.Optional;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.TRANSACTION_ID_KEY;
import static uk.gov.companieshouse.acsp.util.Constants.TRANSACTION_KEY;


@RestController
public class AcspController {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

    @Autowired
    private AcspService acspService;


    @PostMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/authorised-corporate-service-provider-applications")
    public ResponseEntity<Object> createAcspData(
            @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction,
            @RequestBody AcspDataDto acspData,
            HttpServletRequest request) {
        LOGGER.info("received POST request to save acsp data");
        try {
            String requestId = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
            return acspService.createAcspRegData(transaction, acspData, requestId);
        } catch (Exception e) {
            LOGGER.error("Error creating record " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/authorised-corporate-service-provider-applications/{acsp_application_id}")
    public ResponseEntity<Object> saveAcspData(
            @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction,
            @RequestBody AcspDataDto acspData,
            HttpServletRequest request) throws ServiceException {
        LOGGER.info("received request to PUT acsp data");
        try {
            String requestId = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
            return acspService.updateACSPDetails(transaction, acspData, requestId, acspData.getId());
        } catch (SubmissionNotLinkedToTransactionException | InvalidTransactionStatusException e) {
            LOGGER.error("Error updating record " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/authorised-corporate-service-provider-applications/{acsp_application_id}")
    public ResponseEntity<Object> getAcspData(@PathVariable("acsp_application_id") String acspId,
                                              @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction) {
        LOGGER.info("received request to get acsp data");
        Optional<AcspDataDto> acspData;
        try {
            acspData = acspService.getAcsp(acspId, transaction);
        } catch (SubmissionNotLinkedToTransactionException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        if (acspData.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return ResponseEntity.ok().body(acspData);
        }
    }

    @GetMapping("/acsp-api/user/{acsp_application_id}/application")
    public ResponseEntity<Object> checkHasApplication(@PathVariable("acsp_application_id") String acspId,
                                                      HttpServletRequest request) {
        LOGGER.info("received request to check for user applications");
        String requestId = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        return acspService.getAcspApplicationStatus(acspId, requestId);
    }

    @DeleteMapping("/acsp-api/user/{acsp_application_id}/application")
    public ResponseEntity<Object> deleteApplication(@PathVariable("acsp_application_id") String acspId) {
        LOGGER.info("received request to delete application for id: " + acspId);
        return acspService.deleteAcspApplication(acspId);
    }

    @DeleteMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/authorised-corporate-service-provider-applications/{acsp_application_id}")
    public ResponseEntity<Object> deleteApplicationInfo(@PathVariable("acsp_application_id") String acspId,
                                                        @PathVariable(value = TRANSACTION_ID_KEY) String transaction_id,
                                                        @RequestAttribute(value = TRANSACTION_KEY) Transaction transaction) {
        LOGGER.info("Received request to delete application for id: " + acspId);

        if (transaction == null || !transaction.getId().equals(transaction_id)) {
            LOGGER.error("Transaction validation failed for ID: " + transaction_id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ResponseEntity<Object> response = acspService.deleteAcspApplication(acspId);
        if (response.getStatusCode().is2xxSuccessful()) {
            LOGGER.info("Successfully deleted application with ID: " + acspId);
        } else {
            LOGGER.info("Failed to delete application with ID: " + acspId);
        }
        return response;

    }
}
