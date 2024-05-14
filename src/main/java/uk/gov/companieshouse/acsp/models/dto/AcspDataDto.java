package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.acsp.models.dao.AddressDao;
import uk.gov.companieshouse.acsp.models.dao.AmlSupervisoryBodyDao;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

import java.util.Date;
import java.util.Map;


public class AcspDataDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("typeOfBusiness")
    private TypeOfBusiness typeOfBusiness;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("roleType")
    private RoleType roleType;

    @JsonProperty("dateOfBirth")
    private Date dateOfBirth;

    @JsonProperty("verified")
    private Boolean verified;

    @JsonProperty("nationality")
    private NationalityDto[] nationality; // no need of array

    @JsonProperty("countryOfResidence")
    private String countryOfResidence;

    @JsonProperty("businessName")
    private String businessName;

    @JsonProperty("workSector")
    private SectorOfWorkDto workSector;

    @JsonProperty("companyAuthCodeProvided")
    private boolean companyAuthCodeProvided;

    @JsonProperty("companyDetails")
    private CompanyDto companyDetails;

    @JsonProperty("name_registered_with_Aml")
    private String nameRegisteredWithAml;

    @JsonProperty("correspondence_Address")
    private AddressDto correspondenceAddress;

    @JsonProperty("business_Address")
    private AddressDto businessAddress;

    @JsonProperty("aml_supervisory_bodies")
    private AmlSupervisoryBodyDto[] amlSupervisoryBodies;

    @JsonProperty("acspDataSubmission")
    private AcspDataSubmissionDto acspDataSubmission;

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

    public NationalityDto[] getNationality() {
        return nationality;
    }

    public void setNationality(NationalityDto[] nationality) {
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

    public SectorOfWorkDto getWorkSector() {
        return workSector;
    }

    public void setWorkSector(SectorOfWorkDto workSector) {
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

    public String getNameRegisteredWithAml() {
        return nameRegisteredWithAml;
    }

    public void setNameRegisteredWithAml(String nameRegisteredWithAml) {
        this.nameRegisteredWithAml = nameRegisteredWithAml;
    }

    public AddressDto getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(AddressDto correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public AddressDto getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(AddressDto businessAddress) {
        this.businessAddress = businessAddress;
    }

    public AmlSupervisoryBodyDto[] getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }

    public void setAmlSupervisoryBodies(AmlSupervisoryBodyDto[] amlSupervisoryBodies) {
        this.amlSupervisoryBodies = amlSupervisoryBodies;
    }
}
