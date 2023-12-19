package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.service.ACSPService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.environment.EnvironmentReader;

import java.io.IOException;


@RestController
public class PlaceholderController {
    private static final UriTemplate GET_TRANSACTIONS_URI = new UriTemplate("/transactions/{id}");
    @Autowired
    private ACSPService transactionService;

    @GetMapping(value = "/transaction/{id}")
    public ResponseEntity test(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        String transactionsUri = GET_TRANSACTIONS_URI.expand(id).toString();
        Transaction transaction = transactionService.getTransaction(request, transactionsUri);
        return ResponseEntity.ok(transaction);
    }
}
