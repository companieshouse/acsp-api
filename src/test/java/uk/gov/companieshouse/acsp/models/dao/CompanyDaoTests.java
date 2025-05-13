package uk.gov.companieshouse.acsp.models.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanyDaoTests {

    @Test
    void correctlySetsAndGetsCompanyName() {
        CompanyDao company = new CompanyDao();
        company.setCompanyName("Test Company Ltd");
        assertEquals("Test Company Ltd", company.getCompanyName());
    }

    @Test
    void correctlySetsAndGetsCompanyNumber() {
        CompanyDao company = new CompanyDao();
        company.setCompanyNumber("12345678");
        assertEquals("12345678", company.getCompanyNumber());
    }

    @Test
    void returnsNullForUnsetFields() {
        CompanyDao company = new CompanyDao();
        assertNull(company.getCompanyName());
        assertNull(company.getCompanyNumber());
    }
}