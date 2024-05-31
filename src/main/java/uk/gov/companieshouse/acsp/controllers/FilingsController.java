package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.service.FilingsService;
import uk.gov.companieshouse.acsp.util.ApiLogger;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import static uk.gov.companieshouse.acsp.util.Constants.*;

@RestController
@RequestMapping("/private/transactions/{transaction_id}/acsp/{acsp_application_id}/filings")
public class FilingsController {

    @Autowired
    private FilingsService filingService;

    @GetMapping
    public ResponseEntity<FilingApi[]> getFiling(
            @PathVariable(ACSP_APPLICATION_ID_KEY) String acspApplicationId,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId,
            HttpServletRequest request) throws ServiceException {

        String passThroughTokenHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        try {
            FilingApi filing = filingService.generateAcspApplicationFiling(acspApplicationId, transactionId, passThroughTokenHeader);
            return ResponseEntity.ok(new FilingApi[]{filing});
        } catch (Exception e) {
            ApiLogger.errorContext(requestId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}