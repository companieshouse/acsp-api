package uk.gov.companieshouse.acsp.models.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

import java.util.Map;

@Document(collection = "applications")
public class AcspDataDao {

    @Id
    private String id;

    @Field("applicant_details")
    private ApplicantDetailsDao applicantDetails;

    @Field("registered_office_address")
    private AddressDao registeredOfficeAddress;

    @Field("type_of_business")
    private String typeOfBusiness;

    @Field("role_type")
    private RoleType roleType;

    @Field("verified")
    private Boolean verified;

    @Field("business_name")
    private String businessName;

    @Field("work_sector")
    private String workSector;

    @Field("aml_supervisory_bodies")
    private AMLSupervisoryBodiesDao[] amlSupervisoryBodies;

    @Field("company_details")
    private CompanyDao companyDetails;

    @Field("company_authcode_provided")
    private boolean companyAuthCodeProvided;

    @Field("acsp_data_submission")
    private AcspDataSubmissionDao acspDataSubmission;

    @JsonProperty("links")
    private Map<String, String> links;

    @JsonProperty("how_are_you_registered_with_aml")
    private String howAreYouRegisteredWithAml;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApplicantDetailsDao getApplicantDetails() {
        return applicantDetails;
    }

    public void setApplicantDetails(ApplicantDetailsDao applicantDetails) {
        this.applicantDetails = applicantDetails;
    }

    public AddressDao getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(AddressDao registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public String getTypeOfBusiness() {
        return typeOfBusiness;
    }

    public void setTypeOfBusiness(String typeOfBusiness) {
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

    public AMLSupervisoryBodiesDao[] getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }

    public void setAmlSupervisoryBodies(AMLSupervisoryBodiesDao[] amlSupervisoryBodies) {
        this.amlSupervisoryBodies = amlSupervisoryBodies;
    }

    public CompanyDao getCompanyDetails() {
        return companyDetails;
    }

    public void setCompanyDetails(CompanyDao companyDetails) {
        this.companyDetails = companyDetails;
    }

    public boolean isCompanyAuthCodeProvided() {
        return companyAuthCodeProvided;
    }

    public void setCompanyAuthCodeProvided(boolean companyAuthCodeProvided) {
        this.companyAuthCodeProvided = companyAuthCodeProvided;
    }

    public AcspDataSubmissionDao getAcspDataSubmission() {
        return acspDataSubmission;
    }

    public void setAcspDataSubmission(AcspDataSubmissionDao acspDataSubmission) {
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
