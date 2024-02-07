package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import java.io.IOException;

@Service
public class CompanyProfileService {

    private final ApiClientService apiClientService;

    @Autowired
    public CompanyProfileService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }
    public CompanyProfileApi getCompany(String passThroughHeader, String companyUri) throws ServiceException {

        try{
            ApiClient apiClient = apiClientService.getApiClient(passThroughHeader);
            return apiClient.company().get(companyUri).execute().getData();
        } catch (ApiErrorResponseException e){
            throw new ServiceException(e.getStatusCode(), e.getStatusMessage());
        } catch (URIValidationException | IOException e) {
            throw new ServiceException("Error Retrieving Company profile info ", e);
        }

    }
}
