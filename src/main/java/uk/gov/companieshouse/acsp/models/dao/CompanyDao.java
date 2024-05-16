package uk.gov.companieshouse.acsp.models.dao;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.acsp.models.type.Address;
import uk.gov.companieshouse.api.appointment.ServiceAddress;

public class CompanyDao {

    @Field("company_name")
    private String companyName;

    @Field("company_number")
    private String companyNumber;

    @Field("status")
    private String status;

    @Field("incorporation_date")
    private String incorporationDate;

    @Field("company_type")
    private String companyType;

    @Field("registered_office_address")
    private Address registeredOfficeAddress;

    @Field("correspondence_address")
    private ServiceAddress correspondenceAddress;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIncorporationDate() {
        return incorporationDate;
    }

    public void setIncorporationDate(String incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public Address getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(Address registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public ServiceAddress getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(ServiceAddress correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }
}
