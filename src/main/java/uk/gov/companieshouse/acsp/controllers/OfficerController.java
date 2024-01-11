package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.OfficerService;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@RestController
public class OfficerController {
    @Autowired
    private OfficerService officerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

    @GetMapping(value = "/company/{companyNumber}/officers")
    public ResponseEntity getOfficers(@PathVariable String companyNumber, HttpServletRequest request) {
        try {
            String passThroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
            OfficersApi officersApi = officerService.getOfficers(passThroughHeader, companyNumber);
            return ResponseEntity.ok(officersApi);
        } catch (ServiceException e) {
            LOGGER.error("Error Retrieving directors for "+ companyNumber + " Error: "+ e.getCause().getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
