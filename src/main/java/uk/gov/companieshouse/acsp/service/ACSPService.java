package uk.gov.companieshouse.acsp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.repositories.ACSPRepository;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;

@Service
public class ACSPService {
    @Autowired
    ACSPRepository acspRepository;

    @Autowired
    private ApiClientService apiClientService;

    public Transaction getTransaction(HttpServletRequest request, String transactionsUri) throws IOException, URIValidationException {

        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        ApiClient apiClient = apiClientService.getApiClient(passthroughHeader);
        return apiClient.transactions().get(transactionsUri).execute().getData();
    }
}
