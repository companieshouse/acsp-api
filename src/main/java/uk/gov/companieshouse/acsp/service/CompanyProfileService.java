package uk.gov.companieshouse.acsp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;

@Service
public class CompanyProfileService {

    private final ApiClientService apiClientService;

    @Autowired
    public CompanyProfileService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }
    public CompanyProfileApi getCompany(HttpServletRequest request, String companyUri) throws IOException, URIValidationException {

        String passthroughHeader = request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader());
        ApiClient apiClient = apiClientService.getApiClient(passthroughHeader);
        return apiClient.company().get(companyUri).execute().getData();
    }

}
