package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import java.io.IOException;

@Service
public class TransactionService {

    private final ApiClientService apiClientService;
    private static final UriTemplate TRANSACTIONS_URI = new UriTemplate("/transactions/{id}");

    @Autowired
    public TransactionService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public Transaction getTransaction(String passThroughHeader, String id) throws ServiceException {
        try {
            var transactionsUri = TRANSACTIONS_URI.expand(id).toString();
            var apiClient = apiClientService.getApiClient(passThroughHeader);
            //apiClient.setBasePath("https://api.cidev.aws.chdev.org"); //This is for local testing
            return apiClient.transactions().get(transactionsUri).execute().getData();
        } catch (URIValidationException | IOException e) {
            throw new ServiceException("Error Retrieving Transaction " + id, e);
        }
    }

    public void updateTransaction(String passThroughHeader, Transaction transaction) throws ServiceException {
        try {
            var uri = "/private/transactions/" + transaction.getId();
            var resp = apiClientService.getInternalApiClient(passThroughHeader).privateTransaction().patch(uri, transaction).execute();
            if (resp.getStatusCode() != 204) {
                throw new IOException("Invalid Status Code received: " + resp.getStatusCode());
            }
        } catch (IOException | URIValidationException e) {
            var message = "Error Updating Transaction " + transaction.getId();
            throw new ServiceException(message, e);
        }
    }

    public void deleteTransaction(String passThroughHeader, String transactionId) throws ServiceException {
        try {
            var uri = "/transactions/" + transactionId;
            var resp = apiClientService.getApiClient(passThroughHeader)
                    .transactions().delete(uri)
                    .execute();
            if (resp.getStatusCode() != 204) {
                throw new IOException("Invalid Status Code received: " + resp.getStatusCode());
            }
        } catch (IOException | URIValidationException e) {
            var message = "Error deleting Transaction " + transactionId;
            throw new ServiceException(message, e);
        }
    }

}
