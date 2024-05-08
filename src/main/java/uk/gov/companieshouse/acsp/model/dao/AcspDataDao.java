package uk.gov.companieshouse.acsp.model.dao;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.acsp.model.enums.RoleType;
import uk.gov.companieshouse.acsp.model.enums.TypeOfBusiness;

import java.util.Date;
import java.util.Map;

@Document(collection = "applications")
public class AcspDataDao {

    @Id
    private String id;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("addresses")
    private AddressDao[] addresses; // need to match with web

    @Field("type_of_business")
    private TypeOfBusiness typeOfBusiness;

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

    @Field("aml_supervisory_bodies")
    private Map<AMLSupervisoryBodiesDao, Integer> amlSupervisoryBodies;

    @Field("company_details")
    private CompanyDao companyDetails;

    @Field("company_authcode_provided")
    private boolean companyAuthCodeProvided;

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

    public AddressDao[] getAddresses() {
        return addresses;
    }

    public void setAddresses(AddressDao[] addresses) {
        this.addresses = addresses;
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

    public Map<AMLSupervisoryBodiesDao, Integer> getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }

    public void setAmlSupervisoryBodies(Map<AMLSupervisoryBodiesDao, Integer> amlSupervisoryBodies) {
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
}
