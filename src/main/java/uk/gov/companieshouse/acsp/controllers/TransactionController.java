package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_CS;

@RestController
public class TransactionController {
    private static final UriTemplate TRANSACTIONS_URI = new UriTemplate("/transactions/{id}");
    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/transaction/{id}")
    public ResponseEntity getTransaction(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        String transactionsUri = TRANSACTIONS_URI.expand(id).toString();
        String passThroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        Transaction transaction = transactionService.getTransaction(passThroughHeader, transactionsUri);
        return ResponseEntity.ok(transaction);
    }

    @PatchMapping(value = "/transaction/patch/{id}")
    public ResponseEntity<Object> patchTransaction(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        try {
            String transactionsUri = TRANSACTIONS_URI.expand(id).toString();
            String passThroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
            Transaction transaction = transactionService.getTransaction(passThroughHeader, transactionsUri);
            updatedTransaction(transaction);
            transactionService.updateTransaction(passThroughHeader, transaction);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void updatedTransaction(Transaction transaction) {
        String createdUri = "/transactions/" + transaction.getId() + "/acsp/" + "tempACSPId";
        String costUri = "/transactions/" + transaction.getId() + "/acsp/" + "tempACSPId" + "/costs";
        var csResource = new Resource();
        csResource.setKind(FILING_KIND_CS);
        Map<String, String> linksMap = new HashMap<>();
        linksMap.put("resource", createdUri);
        linksMap.put("costs", costUri);
        csResource.setLinks(linksMap);
        transaction.setResources(Collections.singletonMap(createdUri, csResource));
    }

    @PutMapping(value = "/transaction/close/{id}")
    public ResponseEntity<Object> closeTransaction(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        try {
            String transactionsUri = TRANSACTIONS_URI.expand(id).toString();
            String passThroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
            Transaction transaction = transactionService.getTransaction(passThroughHeader, transactionsUri);
            boolean isPaymentRequired =  transactionService.closeTransaction(transaction);
            return ResponseEntity.ok().body(isPaymentRequired);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
