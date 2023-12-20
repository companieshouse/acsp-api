package uk.gov.companieshouse.acsp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;

@Service
public class TransactionService {

    private final ApiClientService apiClientService;

    @Autowired
    public TransactionService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public Transaction getTransaction(String transactionId, String passthroughHeader) throws ServiceException {
        try {
            var uri = "/transactions/" + transactionId;
            return apiClientService.getApiClient(passthroughHeader).transactions().get(uri).execute().getData();
        } catch (URIValidationException | IOException e) {
            throw new ServiceException("Error Retrieving Transaction " + transactionId, e);
        }
    }

    public Transaction getTransaction(HttpServletRequest request, String transactionsUri) throws IOException, URIValidationException {

        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        ApiClient apiClient = apiClientService.getApiClient(passthroughHeader);
        return apiClient.transactions().get(transactionsUri).execute().getData();
    }

    public void updateTransaction(Transaction transaction, String passthroughHeader) throws ServiceException {
        try {
            var uri = "/private/transactions/" + transaction.getId();
            var resp = apiClientService.getInternalApiClient(passthroughHeader).privateTransaction().patch(uri, transaction).execute();

            if (resp.getStatusCode() != 204) {
                throw new IOException("Invalid Status Code received: " + resp.getStatusCode());
            }
        } catch (IOException | URIValidationException e) {
            throw new ServiceException("Error Updating Transaction " + transaction.getId(), e);
        }
    }
}
