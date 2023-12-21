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

    private static final UriTemplate POST_PAYMENT_URI = new UriTemplate("/payments");

    private static final UriTemplate GET_PAYMENT_URI = new UriTemplate("/payments/{payment-id}");
    @Autowired
    private PaymentService paymentService;

    @PostMapping(value = "/payments")
    public ResponseEntity createPayment(@Valid @RequestBody PaymentSessionApi paymentSessionApi) throws ApiErrorResponseException, URIValidationException {
        String paymentUri = POST_PAYMENT_URI.toString();
        PaymentApi paymentApi = paymentService.createPaymentStatus(paymentUri, paymentSessionApi);
        return ResponseEntity.ok(paymentApi);
    }

    @GetMapping(path = "/payments/{payment-id}")
    public ResponseEntity getPayment(@PathVariable("payment-id") String paymentId) throws ApiErrorResponseException, URIValidationException {
        String getPaymentUri = GET_PAYMENT_URI.expand(paymentId).toString();
        PaymentApi payment = paymentService.getPayment(getPaymentUri);
        return ResponseEntity.ok(payment);
    }

}
