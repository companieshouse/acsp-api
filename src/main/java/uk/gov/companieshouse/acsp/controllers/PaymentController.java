package uk.gov.companieshouse.acsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.model.PaymentDataRequest;
import uk.gov.companieshouse.acsp.service.PaymentService;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.payment.PaymentSessionApi;

import javax.validation.Valid;

@RestController
public class PaymentController {

    private static final UriTemplate GET_PAYMENT_URI = new UriTemplate("/payments");
    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/payments")
    public ResponseEntity createPayment(@Valid @RequestBody PaymentSessionApi paymentSessionApi) throws ApiErrorResponseException, URIValidationException {
        String paymentUri = GET_PAYMENT_URI.toString();
        PaymentApi paymentApi = paymentService.createPaymentStatus(paymentUri, paymentSessionApi);
        return ResponseEntity.ok(paymentApi);
    }

    @GetMapping(path = "/{transaction-id}/payments")
    public PaymentApi getPayment(@PathVariable("transaction-id") String transactionId) throws ServiceException {
        PaymentApi payment = paymentService.getPayment(transactionId);
        return payment;
    }

}
