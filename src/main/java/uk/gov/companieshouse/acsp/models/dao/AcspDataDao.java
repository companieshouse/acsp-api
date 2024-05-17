package uk.gov.companieshouse.acsp.models.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

import java.util.Date;
import java.util.Map;

@Document(collection = "applications")
public class AcspDataDao {

    @Id
    private String id;

    @Field("type_of_business")
    private TypeOfBusiness typeOfBusiness;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("role_type")
    private RoleType roleType;

    @Field("date_of_birth")
    private Date dateOfBirth;

    @Field("verified")
    private Boolean verified;

    @Field("nationality")
    private NationalityDao[] nationality; // no need of array

    @Field("country_of_residence")
    private String countryOfResidence;

    @Field("business_name")
    private String businessName;

    @Field("work_sector")
    private SectorOfWorkDao workSector;

    @Field("company_authcode_provided")
    private boolean companyAuthCodeProvided;

    @Field("company_details")
    private CompanyDao companyDetails;

    @Field("name_registered_with_Aml")
    private String nameRegisteredWithAml;

    @Field("correspondence_Address")
    private AddressDao correspondenceAddress;

    @Field("business_Address")
    private AddressDao businessAddress;

    @Field("aml_supervisory_bodies")
    private AmlSupervisoryBodyDao[] amlSupervisoryBodies;

    @Field("acsp_data_submission")
    private AcspDataSubmissionDao acspDataSubmission;

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

    public NationalityDao[] getNationality() {
        return nationality;
    }

    public void setNationality(NationalityDao[] nationality) {
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

    public SectorOfWorkDao getWorkSector() {
        return workSector;
    }

    public void setWorkSector(SectorOfWorkDao workSector) {
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

    public String getNameRegisteredWithAml() {
        return nameRegisteredWithAml;
    }

    public void setNameRegisteredWithAml(String nameRegisteredWithAml) {
        this.nameRegisteredWithAml = nameRegisteredWithAml;
    }

    public AddressDao getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(AddressDao correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public AddressDao getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(AddressDao businessAddress) {
        this.businessAddress = businessAddress;
    }

    public void setAmlSupervisoryBodies(AmlSupervisoryBodyDao[] amlSupervisoryBodies) {
        this.amlSupervisoryBodies = amlSupervisoryBodies;
    }

    public AmlSupervisoryBodyDao[] getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }
}
