package uk.gov.companieshouse.acsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.acsp.model.AcspData;
import uk.gov.companieshouse.acsp.service.AcspService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@RestController
public class AcspController {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    @Autowired
    private AcspService acspService;

    @PutMapping("/acsp")
    public ResponseEntity<AcspData> saveAcspData(@RequestBody AcspData acspData){
        LOGGER.debug("received request to save acsp data");
        return new ResponseEntity<>(acspService.saveOrUpdateAcsp(acspData), HttpStatus.OK);
    }
}
