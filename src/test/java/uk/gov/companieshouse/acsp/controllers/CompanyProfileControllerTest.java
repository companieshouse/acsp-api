package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.service.CompanyProfileService;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyProfileControllerTest {

    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    private HttpServletRequest request;
    @InjectMocks
    private CompanyProfileController companyProfileController;

    private static final UriTemplate GET_COMPANY_URI = new UriTemplate("/company/{id}");
    private static final String COMPANY_NUMBER = "12345678";

    @Test
    void getCompany() throws ServiceException{
        var companyProfile = new CompanyProfileApi();
        companyProfile.setCompanyNumber("12345678");

        String companyUri = GET_COMPANY_URI.expand(COMPANY_NUMBER).toString();
        when(companyProfileService.getCompany(any(), any())).thenReturn(companyProfile);
        var response = companyProfileController.getCompany(companyUri, request);

        assertEquals("12345678", ((CompanyProfileApi) response.getBody()).getCompanyNumber());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getCompany_notFoundError() throws ServiceException {
        String companyUri = GET_COMPANY_URI.expand(COMPANY_NUMBER).toString();
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(null);
        when(companyProfileService.getCompany(null,companyUri)).thenThrow(new ServiceException(404, "company ID not found"));
        var response = companyProfileController.getCompany(COMPANY_NUMBER, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

