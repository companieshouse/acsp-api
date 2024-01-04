package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceTest {

    @Mock
    private ApiClientService apiClientService;
    @Mock
    private ApiClient apiClient;
    @Mock
    private CompanyResourceHandler companyResourceHandler;
    @Mock
    private ApiResponse<CompanyProfileApi> apiResponse;
    @InjectMocks
    private CompanyProfileService companyProfileService;
    private static final String COMPANY_NUMBER = "12345678";
    private static final String PASS_THROUGH_HEADER = "passthrough";
    @Mock
    private CompanyGet companyGet;

        @Test
        void testGetCompany() throws IOException, URIValidationException {
            CompanyProfileApi companyProfile = new CompanyProfileApi();
            companyProfile.setCompanyNumber(COMPANY_NUMBER);

            when(apiClientService.getApiClient(PASS_THROUGH_HEADER)).thenReturn(apiClient);
            when(apiClient.company()).thenReturn(companyResourceHandler);
            when(companyResourceHandler.get(COMPANY_NUMBER)).thenReturn(companyGet);
            when(companyGet.execute()).thenReturn(apiResponse);
            when(apiResponse.getData()).thenReturn(companyProfile);

            var response = companyProfileService.getCompany(PASS_THROUGH_HEADER, COMPANY_NUMBER);

            assertEquals(companyProfile, response);


        }

}