package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.acsp.models.type.Address;
import uk.gov.companieshouse.api.appointment.ServiceAddress;

public class CompanyDto {

    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("companyNumber")
    private String companyNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("incorporationDate")
    private String incorporationDate;

    @JsonProperty("companyType")
    private String companyType;

    @JsonProperty("registeredOfficeAddress")
    private Address registeredOfficeAddress;

    @JsonProperty("correspondenceAddress")
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
