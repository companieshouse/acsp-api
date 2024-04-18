package uk.gov.companieshouse.acsp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Document(collection = "applications")
public class AcspData {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Address[] addresses;
    private TypeOfBusiness typeofBusiness;
    private RoleType roleType;
    private Date dateOfBirth;
    private Boolean verified;
    private Nationality[] nationality;
    private String countryOfResidence;
    private String businessName;
    private SectorOfWork workSector;
    private Map<AMLSupervisoryBodies, Integer> amlSupervisoryBodies;
    private Company companyDetails;
    private boolean companyAuthCodeProvided;

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

    public Address[] getAddresses() {
        return addresses;
    }

    public void setAddresses(Address[] addresses) {
        this.addresses = addresses;
    }

    public TypeOfBusiness getTypeofBusiness() {
        return typeofBusiness;
    }

    public void setTypeofBusiness(TypeOfBusiness typeofBusiness) {
        this.typeofBusiness = typeofBusiness;
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

    public Nationality[] getNationality() {
        return nationality;
    }

    public void setNationality(Nationality[] nationality) {
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

    public SectorOfWork getWorkSector() {
        return workSector;
    }

    public void setWorkSector(SectorOfWork workSector) {
        this.workSector = workSector;
    }

    public Map<AMLSupervisoryBodies, Integer> getAmlSupervisoryBodies() {
        return amlSupervisoryBodies;
    }

    public void setAmlSupervisoryBodies(Map<AMLSupervisoryBodies, Integer> amlSupervisoryBodies) {
        this.amlSupervisoryBodies = amlSupervisoryBodies;
    }

    public Company getCompanyDetails() {
        return companyDetails;
    }

    public void setCompanyDetails(Company companyDetails) {
        this.companyDetails = companyDetails;
    }

    public boolean isCompanyAuthCodeProvided() {
        return companyAuthCodeProvided;
    }

    public void setCompanyAuthCodeProvided(boolean companyAuthCodeProvided) {
        this.companyAuthCodeProvided = companyAuthCodeProvided;
    }
}
