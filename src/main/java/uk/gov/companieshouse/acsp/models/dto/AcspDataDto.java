package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

import java.util.Map;

public class AcspDataDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("applicantDetails")
    private ApplicantDetailsDto applicantDetails;

    @JsonProperty("registeredOfficeAddress")
    private AddressDto registeredOfficeAddress;

    @JsonProperty("typeOfBusiness")
    private TypeOfBusiness typeOfBusiness;

    @JsonProperty("roleType")
    private RoleType roleType;

    @JsonProperty("verified")
    private Boolean verified;

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

    @JsonProperty("links")
    private Map<String, String> links;

    @JsonProperty("howAreYouRegisteredWithAml")
    private String howAreYouRegisteredWithAml;

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApplicantDetailsDto getApplicantDetails() {
        return applicantDetails;
    }

    public void setApplicantDetails(ApplicantDetailsDto applicantDetails) {
        this.applicantDetails = applicantDetails;
    }

    public AddressDto getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(AddressDto registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
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

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
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

    public AMLSupervisoryBodiesDto[] getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }

    public void setAmlSupervisoryBodies(AMLSupervisoryBodiesDto[] amlSupervisoryBodies) {
        this.amlSupervisoryBodies = amlSupervisoryBodies;
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

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    public String getHowAreYouRegisteredWithAml() {
        return howAreYouRegisteredWithAml;
    }

    public void setHowAreYouRegisteredWithAml(String howAreYouRegisteredWithAml) {
        this.howAreYouRegisteredWithAml = howAreYouRegisteredWithAml;
    }
}
