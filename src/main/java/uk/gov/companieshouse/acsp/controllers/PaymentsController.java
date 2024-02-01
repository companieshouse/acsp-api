package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.PaymentsService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@RestController
public class PaymentsController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PaymentsService paymentsService;

    @PostMapping(value = "/transaction/payments/{id}")
    public ResponseEntity<Object> getPaymentDetails(@PathVariable String id, HttpServletRequest request) {
        try {
            String passThroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
            Transaction transaction = transactionService.getTransaction(passThroughHeader, id);

            PaymentApi paymentsDetails = paymentsService.createPaymentSession(passThroughHeader, transaction);

            return ResponseEntity.ok().body(paymentsDetails);
        } catch (ServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
