package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.service.CompanyProfileService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import java.io.IOException;

@RestController
public class CompanyProfileController {
    private static final UriTemplate GET_COMPANY_URI = new UriTemplate("/company/{id}");
    @Autowired
    private CompanyProfileService companyApiService;

    @GetMapping(value = "/company/{id}")
    public ResponseEntity getCompany(@PathVariable String id, HttpServletRequest request) throws IOException, URIValidationException {
        String transactionsUri = GET_COMPANY_URI.expand(id).toString();
        CompanyProfileApi companyProfile = companyApiService.getCompany(request, transactionsUri);
        return ResponseEntity.ok(companyProfile);
    }
}
