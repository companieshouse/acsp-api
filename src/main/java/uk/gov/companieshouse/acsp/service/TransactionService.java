package uk.gov.companieshouse.acsp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_CS;

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

    public void updateTransaction(HttpServletRequest request, Transaction transaction) throws ServiceException {
        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        try {
            var uri = "/private/transactions/" + transaction.getId();

            String createdUri = "/transactions/" + transaction.getId() + "/acsp/" + "tempACSPId";
            String costUri = "/transactions/" + transaction.getId() + "/acsp/" + "tempACSPId" + "/costs";
            var csResource = new Resource();
            csResource.setKind(FILING_KIND_CS);
            Map<String, String> linksMap = new HashMap<>();
            linksMap.put("resource", createdUri);
            linksMap.put("costs", costUri);
            csResource.setLinks(linksMap);
            transaction.setResources(Collections.singletonMap(createdUri, csResource));

            var resp = apiClientService.getInternalApiClient(passthroughHeader).privateTransaction().patch(uri, transaction).execute();

            if (resp.getStatusCode() != 204) {
                throw new IOException("Invalid Status Code received: " + resp.getStatusCode());
            }
        } catch (IOException | URIValidationException e) {
            throw new ServiceException("Error Updating Transaction " + transaction.getId(), e);
        }
    }
}
