package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.type.Address;

import java.util.Date;


public class AcspDataDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("correspondenceAddress")
    private Address correspondenceAddresses;

    @JsonProperty("businessAddress")
    private Address businessAddress;

    @JsonProperty("typeOfBusiness")
    private TypeOfBusiness typeOfBusiness;

    @JsonProperty("roleType")
    private RoleType roleType;

    @JsonProperty("dateOfBirth")
    private Date dateOfBirth;

    @JsonProperty("verified")
    private Boolean verified;

    @JsonProperty("nationality")
    private NationalityDto nationality;

    @JsonProperty("countryOfResidence")
    private String countryOfResidence;

    @JsonProperty("businessName")
    private String businessName;

    @JsonProperty("workSector")
    private String workSector;

    @JsonProperty("amlSupervisoryBodies")
    private AMLSupervisoryBodiesDto[] amlSupervisoryBodies;

    @JsonProperty("companyDetails")
    private CompanyDto companyDetails;

    @JsonProperty("companyAuthCodeProvided")
    private boolean companyAuthCodeProvided;

    @JsonProperty("acspDataSubmission")
    private AcspDataSubmissionDto acspDataSubmission;

    @JsonProperty("howAreYouRegisteredWithAML")
    private String howAreYouRegisteredWithAML;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getCorrespondenceAddresses() {
        return correspondenceAddresses;
    }

    public void setCorrespondenceAddresses(Address correspondenceAddresses) {
        this.correspondenceAddresses = correspondenceAddresses;
    }

    public Address getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(Address businessAddress) {
        this.businessAddress = businessAddress;
    }

    public TypeOfBusiness getTypeOfBusiness() {
        return typeOfBusiness;
    }

    public void setTypeOfBusiness(TypeOfBusiness typeOfBusiness) {
        this.typeOfBusiness = typeOfBusiness;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public NationalityDto getNationality() {
        return nationality;
    }

    public void setNationality(NationalityDto nationality) {
        this.nationality = nationality;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getWorkSector() {
        return workSector;
    }

    public void setWorkSector(String workSector) {
        this.workSector = workSector;
    }

    public CompanyDto getCompanyDetails() {
        return companyDetails;
    }

    public void setCompanyDetails(CompanyDto companyDetails) {
        this.companyDetails = companyDetails;
    }

    public boolean isCompanyAuthCodeProvided() {
        return companyAuthCodeProvided;
    }

    public void setCompanyAuthCodeProvided(boolean companyAuthCodeProvided) {
        this.companyAuthCodeProvided = companyAuthCodeProvided;
    }

    public AcspDataSubmissionDto getAcspDataSubmission() {
        return acspDataSubmission;
    }

    public void setAcspDataSubmission(AcspDataSubmissionDto acspDataSubmission) {
        this.acspDataSubmission = acspDataSubmission;
    }

    public String getHowAreYouRegisteredWithAML() {
        return howAreYouRegisteredWithAML;
    }

    public void setHowAreYouRegisteredWithAML(String howAreYouRegisteredWithAML) {
        this.howAreYouRegisteredWithAML = howAreYouRegisteredWithAML;
    }

    public AMLSupervisoryBodiesDto[] getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }

    public void setAmlSupervisoryBodies(AMLSupervisoryBodiesDto[] amlSupervisoryBodies) {
        this.amlSupervisoryBodies = amlSupervisoryBodies;
    }
}
