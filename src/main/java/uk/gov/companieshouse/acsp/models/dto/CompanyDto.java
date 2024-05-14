package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.appointment.ServiceAddress;

public class CompanyDto {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("incorporation_date")
    private String incorporationDate;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("registered_office_address")
    private Object registeredOfficeAddress;

    @JsonProperty("correspondence_address")
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

    public Object getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(Object registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public ServiceAddress getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(ServiceAddress correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }
}
