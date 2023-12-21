package uk.gov.companieshouse.acsp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.payment.PaymentSessionApi;

@Service
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private static final String EXCEPTION_MESSAGE = "Error Retrieving Payment for transactionId %s";
    private static final String EXCEPTION_MESSAGE_WITH_HTTP_CODE = EXCEPTION_MESSAGE + ", http status code %s";

    private final ApiClientService apiClientService;

    @Autowired
    public PaymentService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public PaymentApi createPaymentStatus(String paymentUri, PaymentSessionApi paymentSessionApi) throws ApiErrorResponseException, URIValidationException {
        ApiClient apiClient = apiClientService.getApiClient();
        return apiClient.payment().create(paymentUri, paymentSessionApi).execute().getData();

    }

    public PaymentApi getPayment(String getPaymentUri) throws ApiErrorResponseException, URIValidationException {
        ApiClient apiClient = apiClientService.getApiClient();
        return apiClient.payment().get(getPaymentUri).execute().getData();
    }
}
