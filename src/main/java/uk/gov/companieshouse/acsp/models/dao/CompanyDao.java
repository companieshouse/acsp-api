package uk.gov.companieshouse.acsp.models.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class CompanyDao {

    @Field("company_name")
    private String companyName;

    @Field("company_number")
    private String companyNumber;


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

}
