package uk.gov.companieshouse.acsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.model.PaymentDataRequest;
import uk.gov.companieshouse.acsp.service.PaymentService;
import uk.gov.companieshouse.api.model.payment.PaymentApi;

import javax.validation.Valid;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(path="/payments")
    public String createPayment(@Valid @RequestBody PaymentDataRequest paymentDataRequest) throws ServiceException {
        String paymentStatus = paymentService.createPaymentStatus(paymentDataRequest);
        return paymentStatus;
    }

    @GetMapping(path = "/{transaction-id}/payments")
    public PaymentApi getPayment(@PathVariable("transaction-id") String transactionId) throws ServiceException {
        PaymentApi payment = paymentService.getPayment(transactionId);
        return payment;
    }

}
