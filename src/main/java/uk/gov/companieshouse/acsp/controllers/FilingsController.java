package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.filing.Filing;
import uk.gov.companieshouse.acsp.service.FilingsService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.ACSP_APPLICATION_ID_KEY;
import static uk.gov.companieshouse.acsp.util.Constants.TRANSACTION_ID_KEY;


@RestController
@RequestMapping("/private/transactions/{transaction_id}/authorised-corporate-service-provider-applications/{acsp_application_id}/filings")
public class FilingsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

    private final FilingsService filingService;

    public FilingsController(FilingsService filingService) {
        this.filingService = filingService;
    }

    @GetMapping
    public ResponseEntity<Filing> getFiling(
            @PathVariable(ACSP_APPLICATION_ID_KEY) String acspApplicationId,
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            HttpServletRequest request) {
        LOGGER.info("received request to get filing data");

        String passThroughTokenHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        try {
            Filing filing = filingService.generateAcspApplicationFiling(acspApplicationId, transactionId, passThroughTokenHeader);
            return ResponseEntity.ok(filing);
        } catch (ServiceException | SubmissionNotLinkedToTransactionException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
