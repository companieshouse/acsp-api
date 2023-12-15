package uk.gov.companieshouse.acsp.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import uk.gov.companieshouse.acsp.Exception.CompanyNotFoundException;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.CompanyProfileApiService;


@RestController
@RequestMapping("/acsp/company-profile")
public class CompanyProfileApiController {

    private final CompanyProfileApiService companyProfileApiService;

    @Autowired
    public CompanyProfileApiController(CompanyProfileApiService companyProfileApiService) {
        this.companyProfileApiService = companyProfileApiService;
    }

    @GetMapping("/{company-number}")
    public String getCompanyNumber(@PathVariable("company-number") String companyNumber) throws ServiceException, CompanyNotFoundException {

       var companyProfileApi =  companyProfileApiService.getCompanyProfile(companyNumber);

        return companyNumber;
    }
}
