package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.service.ACSPService;
import uk.gov.companieshouse.acsp.service.CostService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.payment.Cost;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
public class TransactionController {
    private static final UriTemplate GET_TRANSACTIONS_URI = new UriTemplate("/transactions/{id}");
    @Autowired
    private TransactionService transactionService;

    @GetMapping(value = "/transaction/{id}")
    public ResponseEntity test(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        String transactionsUri = GET_TRANSACTIONS_URI.expand(id).toString();
        Transaction transaction = transactionService.getTransaction(request, transactionsUri);
        return ResponseEntity.ok(transaction);
    }
}
