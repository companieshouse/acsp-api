package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.service.CompanyProfileService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void getCompany() throws IOException, URIValidationException {

        String companyUri = GET_COMPANY_URI.expand(COMPANY_NUMBER).toString();
        var response = companyProfileController.getCompany(companyUri, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}