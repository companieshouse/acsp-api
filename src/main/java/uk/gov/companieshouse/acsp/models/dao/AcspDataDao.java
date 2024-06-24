package uk.gov.companieshouse.acsp.models.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.type.Address;

import java.time.LocalDate;
import java.util.Date;

@Document(collection = "applications")
public class AcspDataDao {

    @Id
    private String id;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("middle_name")
    private String middleName;

    @Field("email")
    private String email;

    @Field("correspondence_address")
    private Address correspondenceAddress;

    @Field("business_address")
    private Address businessAddress;

    @Field("type_of_business")
    private TypeOfBusiness typeOfBusiness;

    @Field("role_type")
    private RoleType roleType;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("verified")
    private Boolean verified;

    @Field("nationality")
    private NationalityDao nationality;

    @Field("country_of_residence")
    private String countryOfResidence;

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

    @JsonProperty("how_are_you_registered_with_aml")
    private String howAreYouRegisteredWithAml;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getCorrespondenceAddresses() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddresses(Address correspondenceAddresses) {
        this.correspondenceAddress = correspondenceAddresses;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public NationalityDao getNationality() {
        return nationality;
    }

    public void setNationality(NationalityDao nationality) {
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

    public AMLSupervisoryBodiesDao[] getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }

    public void setAmlSupervisoryBodies(AMLSupervisoryBodiesDao[] amlSupervisoryBodies) {
        this.amlSupervisoryBodies = amlSupervisoryBodies;
    }

    public Address getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(Address correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public String getHowAreYouRegisteredWithAml() {
        return howAreYouRegisteredWithAml;
    }

    public void setHowAreYouRegisteredWithAml(String howAreYouRegisteredWithAml) {
        this.howAreYouRegisteredWithAml = howAreYouRegisteredWithAml;
    }
}
