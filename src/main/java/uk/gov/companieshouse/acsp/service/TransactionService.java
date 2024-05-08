package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.acsp.util.ApiLogger;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.PAYMENT_REQUIRED_HEADER;
import static uk.gov.companieshouse.acsp.util.Constants.TRANSACTIONS_PRIVATE_API_PREFIX;


@Service
public class TransactionService {

    private final ApiClientService apiClientService;
    private static final UriTemplate TRANSACTIONS_URI = new UriTemplate("/transactions/{id}");
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

    @Autowired
    public TransactionService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public Transaction getTransaction(String passThroughHeader, String id) throws ServiceException {
        try {
            String transactionsUri = TRANSACTIONS_URI.expand(id).toString();
            ApiClient apiClient = apiClientService.getApiClient(passThroughHeader);
            LOGGER.info("Got API client");
            return apiClient.transactions().get(transactionsUri).execute().getData();
        } catch (URIValidationException | IOException e) {
            throw new ServiceException("Error Retrieving Transaction " + id, e);
        }
    }

    public void updateTransaction(Transaction transaction, String loggingContext) throws ServiceException {
        try {
            var uri = TRANSACTIONS_PRIVATE_API_PREFIX + transaction.getId();

            // The internal API key client is used here as the transaction service will call back into the OE API to get
            // the costs (if a costs end-point has already been set on the transaction) and those calls cannot be made
            // with a user token
            InternalApiClient apiClient = apiClientService.getInternalApiClient();
            var patchRequest = apiClient.privateTransaction().patch(uri, transaction);
            LOGGER.info("patchrequest request created");
            var response = patchRequest.execute();

            if (response.getStatusCode() != 204) {
                throw new IOException("Invalid Status Code received from Transactions-api: " + response.getStatusCode());
            }
        } catch (IOException | URIValidationException e) {
            var message = "Error Updating Transaction " + transaction.getId();
            ApiLogger.errorContext(loggingContext, message, e);
            throw new ServiceException(message, e);
        }
    }

    public boolean closeTransaction(Transaction transaction) throws ServiceException {
        try {
            transaction.setStatus(TransactionStatus.CLOSED);
            var uri = "/transactions/" + transaction.getId();
            Map<String, Object> headers =
                    apiClientService.getApiClient()
                            .transactions().update(uri, transaction)
                            .execute().getHeaders();

            boolean paymentRequired = false;
            List<String> paymentRequiredHeaders = (ArrayList) headers.get(PAYMENT_REQUIRED_HEADER);
            if (paymentRequiredHeaders != null) {
                paymentRequired = true;
            }
            return paymentRequired;
        } catch (URIValidationException e) {
            throw new ServiceException("Invalid URI for transactions resource", e);
        } catch (ApiErrorResponseException e) {
            throw new ServiceException("Error closing transaction", e);
        }
    }
}
