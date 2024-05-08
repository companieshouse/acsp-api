package uk.gov.companieshouse.acsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.model.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.service.AcspService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.*;

@RestController
@RequestMapping("/transactions/{" + TRANSACTION_ID_KEY + "}/acsp")
public class AcspController {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    @Autowired
    private AcspService acspService;

    @Autowired
    private TransactionService transactionService;

    @PutMapping
    public ResponseEntity<Object> saveAcspData(
            @PathVariable(TRANSACTION_ID_KEY) String transactionId,
            @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId,
            @RequestHeader(value = ERIC_IDENTITY) String userId,
            @RequestBody AcspDataDto acspData) throws ServiceException {
        LOGGER.info("received request to save acsp data");
        Transaction transaction = transactionService.getTransaction(requestId, transactionId);
        ResponseEntity<Object> responseEntity = acspService.saveOrUpdateAcsp(transaction, acspData, requestId, userId);
        return responseEntity;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAcspData(@PathVariable(TRANSACTION_ID_KEY) String transactionId,
                                              @PathVariable("id") String id,
                                              @RequestHeader(value = ERIC_ACCESS_TOKEN) String requestId)
            throws ServiceException {
        LOGGER.info("received request to get acsp data");
        var acspData = acspService.getAcsp(id);
        if (acspData == null){
            return new ResponseEntity<>( HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(acspData, HttpStatus.OK);
        }
    }

}
