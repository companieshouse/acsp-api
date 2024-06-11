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
    private Address correspondenceAddress;
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
    private AmlMembership[] amlMemberships;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("business_name")
    private String businessName;


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

    public Address getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(Address correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
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

    public AmlMembership[] getAmlMemberships() {
        return amlMemberships;
    }

    public void setAmlMemberships(AmlMembership[] amlMemberships) {
        this.amlMemberships = amlMemberships;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
