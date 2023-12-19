package uk.gov.companieshouse.acsp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.model.PaymentDataReponse;
import uk.gov.companieshouse.acsp.model.PaymentDataRequest;
import uk.gov.companieshouse.acsp.service.PaymentService;

import javax.validation.Valid;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(path="/{transaction-id}/payments")
    public String createPayment(@PathVariable("transaction-id") final String transactionId,
                                            @Valid @RequestBody PaymentDataRequest paymentDataRequest) throws ServiceException {
        String paymentStatus = paymentService.createPaymentStatus(paymentDataRequest, transactionId);
        return paymentStatus;
    }

}
