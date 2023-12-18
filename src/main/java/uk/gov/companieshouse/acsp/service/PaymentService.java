package uk.gov.companieshouse.acsp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.PaymentsClient;
import uk.gov.companieshouse.acsp.model.PaymentDataReponse;
import uk.gov.companieshouse.acsp.model.PaymentDataRequest;

@Service
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentsClient paymentsClient;

    public PaymentService(PaymentsClient paymentsClient) {
        this.paymentsClient = paymentsClient;
    }

    public String paymentStatus(PaymentDataRequest paymentDataRequest, String paymentReference) {
        PaymentDataReponse paymentDataReponse = paymentsClient.createPayment(paymentDataRequest, paymentReference);

        if (paymentDataReponse != null) {
            return paymentDataReponse.getStatus();
        }
        return null;
    }

    public PaymentDataReponse getPayment(PaymentDataRequest paymentDataRequest) {
        return null;
    }
}
