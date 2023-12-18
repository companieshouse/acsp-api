package uk.gov.companieshouse.acsp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import uk.gov.companieshouse.acsp.config.PaymentsConfig;
import uk.gov.companieshouse.acsp.model.PaymentDataReponse;
import org.springframework.web.reactive.function.client.WebClient;
import uk.gov.companieshouse.acsp.model.PaymentDataRequest;

import static uk.gov.companieshouse.acsp.constants.ACSPConstants.*;

@Service
public class PaymentsClient {
    private static class PaymentDetailsException extends RuntimeException {}

    private static final String REFUNDS_URI = "/payments/{paymentReference}/refunds";
    private static final String PAYMENT_DETAILS_URI = "/private/payments/{paymentReference}/payment-details";

    private final PaymentsConfig config;

    @Autowired
    public PaymentsClient(PaymentsConfig config) {
        this.config = config;
    }

//    public RefundResponse refundPayment(RefundRequest data, String paymentReference) {
//        return WebClient
//                .create(config.getPaymentsHost())
//                .post()
//                .uri(REFUNDS_URI, paymentReference)
//                .header(HEADER_AUTHORIZATION, config.getApiKey())
//                .header(HEADER_ACCEPT, CONTENT_TYPE_JSON)
//                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
//                .body(Mono.just(data), RefundRequest.class)
//                .retrieve()
//                .bodyToMono(RefundResponse.class)
//               .block();
//    }

    public PaymentDataReponse createPayment(PaymentDataRequest paymentDataRequest, String paymentReference) {
        try {
            return WebClient
                    .create(config.getPaymentsHost())
                    .post()
                    .uri(PAYMENT_DETAILS_URI, paymentReference)
                    .header(HEADER_AUTHORIZATION, config.getApiKey())
                    .header(HEADER_ACCEPT, CONTENT_TYPE_JSON)
                    .body(Mono.just(paymentDataRequest), PaymentDataRequest.class)
                    .retrieve()
                    .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, clientResponse -> {
                        throw new PaymentDetailsException();
                    })
                    .bodyToMono(PaymentDataReponse.class)
                    .block();
        } catch (PaymentDetailsException ex) {
            return null;
        }
    }
}
