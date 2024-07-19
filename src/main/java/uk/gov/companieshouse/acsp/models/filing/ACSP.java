package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ACSP {

    @JsonProperty("payment_reference")
    private String paymentReference;
    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("correspondence_address")
    private ServiceAddress serviceAddress;
    @JsonProperty("office_address")
    private Address officeAddress;
    @JsonProperty("email")
    private String email;
    @JsonProperty("company_number")
    private String  companyNumber;
    @JsonProperty("company_name")
    private String  companyName;
    @JsonProperty("acsp_type")
    private String acspType;
    @JsonProperty("business_sector")
    private String businessSector;
    @JsonProperty("amlmemberships")
    private Aml aml;
    @JsonProperty("business_name")
    private String businessName;
    @JsonProperty("st_personal_information")
    private STPersonalInformation stPersonalInformation;
    @JsonProperty("isServiceAddressROA")
    private boolean isServiceAddressROA;


    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ServiceAddress getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(ServiceAddress serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public Address getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(Address officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAcspType() {
        return acspType;
    }

    public void setAcspType(String acspType) {
        this.acspType = acspType;
    }

    public String getBusinessSector() {
        return businessSector;
    }

    public void setBusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }

    public Aml getAml() {
        return aml;
    }

    public void setAml(Aml aml) {
        this.aml = aml;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public STPersonalInformation getStPersonalInformation() {
        return stPersonalInformation;
    }

    public void setStPersonalInformation(STPersonalInformation stPersonalInformation) {
        this.stPersonalInformation = stPersonalInformation;
    }

    public boolean isServiceAddressROA() {
        return isServiceAddressROA;
    }

    public void setServiceAddressROA(boolean serviceAddressROA) {
        isServiceAddressROA = serviceAddressROA;
    }
}
