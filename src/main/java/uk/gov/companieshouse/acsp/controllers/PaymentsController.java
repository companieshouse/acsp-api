package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.PaymentsService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;
@RestController
public class PaymentsController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PaymentsService paymentsService;

    @GetMapping(value = "/transaction/payments/{id}")
    public ResponseEntity<Object> closeTransaction(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        try {
            String passThroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
            Transaction transaction = transactionService.getTransaction(passThroughHeader, id);

            PaymentApi paymentsDetails = paymentsService.getPaymentsDetails(passThroughHeader, transaction);

            boolean isPaymentRequired =  transactionService.closeTransaction(transaction);
            return ResponseEntity.ok().body(paymentsDetails);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
