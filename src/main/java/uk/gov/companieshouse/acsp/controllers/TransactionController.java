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
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.io.IOException;

@RestController
public class TransactionController {
    private static final UriTemplate GET_TRANSACTIONS_URI = new UriTemplate("/transactions/{id}");
    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/transaction/{id}")
    public ResponseEntity getTransaction(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        String transactionsUri = GET_TRANSACTIONS_URI.expand(id).toString();
        Transaction transaction = transactionService.getTransaction(request, transactionsUri);
        return ResponseEntity.ok(transaction);
    }

    @PatchMapping(value = "/transaction/patch/{id}")
    public ResponseEntity<Object> patchTransaction(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        try {
            String transactionsUri = GET_TRANSACTIONS_URI.expand(id).toString();
            Transaction transaction = transactionService.getTransaction(request, transactionsUri);

            transactionService.updateTransaction(request, transaction);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
