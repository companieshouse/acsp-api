package uk.gov.companieshouse.acsp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.companieshouse.acsp.util.Constants.PAYMENT_REQUIRED_HEADER;


@Service
public class TransactionService {

    private final ApiClientService apiClientService;

    @Autowired
    public TransactionService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public Transaction getTransaction(String passThroughHeader, String transactionsUri) throws IOException, URIValidationException {
        ApiClient apiClient = apiClientService.getApiClient(passThroughHeader);
        return apiClient.transactions().get(transactionsUri).execute().getData();
    }

    public void updateTransaction(String passThroughHeader, Transaction transaction) throws ServiceException {
        try {
            var uri = "/private/transactions/" + transaction.getId();

            var resp = apiClientService.getInternalApiClient(passThroughHeader).privateTransaction().patch(uri, transaction).execute();

            if (resp.getStatusCode() != 204) {
                throw new IOException("Invalid Status Code received: " + resp.getStatusCode());
            }
        } catch (IOException | URIValidationException e) {
            throw new ServiceException("Error Updating Transaction " + transaction.getId(), e);
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
