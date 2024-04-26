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
import static uk.gov.companieshouse.acsp.util.Constants.TRANSACTION_ID_KEY;

@RestController
@RequestMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/acsp")
public class AcspController {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    @Autowired
    private AcspService acspService;

    @PutMapping
    public ResponseEntity<AcspData> saveAcspData(@RequestBody AcspData acspData){
        LOGGER.info("received request to save acsp data");
        return new ResponseEntity<>(acspService.saveOrUpdateAcsp(acspData), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcspData> getAcspData(@PathVariable String id){
        LOGGER.info("received request to get acsp data");
        AcspData acspData = acspService.getAcsp(id);
        if (acspData == null){
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(acspData, HttpStatus.OK);
        }
    }

}
