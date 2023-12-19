package uk.gov.companieshouse.acsp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.model.PaymentDataReponse;
import uk.gov.companieshouse.acsp.model.PaymentDataRequest;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

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

    public String createPaymentStatus(PaymentDataRequest paymentDataRequest, String transactionId) throws ServiceException {
        try {
            String uri = "/" + transactionId + "/payment";
            return apiClientService.postApiClient(uri, paymentDataRequest).payment().get(uri).execute().getData().getStatus();
        } catch (URIValidationException e) {
            throw new ServiceException(String.format(EXCEPTION_MESSAGE, transactionId), e);
        } catch (ApiErrorResponseException e) {
            if (HttpStatus.NOT_FOUND.value() == e.getStatusCode()) {
                throw new ServiceException("Payment request failed due to " + e.getMessage());
            }
            var message = String.format(
                    EXCEPTION_MESSAGE_WITH_HTTP_CODE,
                    transactionId,
                    e.getStatusCode());
            throw new ServiceException(message, e);
        }
    }
}
