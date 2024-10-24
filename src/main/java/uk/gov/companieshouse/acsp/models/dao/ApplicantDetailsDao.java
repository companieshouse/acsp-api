package uk.gov.companieshouse.acsp.models.dao;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

public class ApplicantDetailsDao {

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Field("middle_name")
    private String middleName;

    @Field("correspondence_address")
    private AddressDao correspondenceAddress;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

    @Field("nationality")
    private NationalityDao nationality;

    @Field("country_of_residence")
    private String countryOfResidence;

    @Field("correspondence_address_is_same_as_registered_office_address")
    private boolean correspondenceAddressIsSameAsRegisteredOfficeAddress;

    @Field("correspondence_email")
    private String correspondenceEmail;

    // Getters and Setters

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

    public AddressDao getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(AddressDao correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public boolean getCorrespondenceAddressIsSameAsRegisteredOfficeAddress() {
        return correspondenceAddressIsSameAsRegisteredOfficeAddress;
    }

    public void setCorrespondenceAddressIsSameAsRegisteredOfficeAddress(boolean correspondenceAddressIsSameAsRegisteredOfficeAddress) {
        this.correspondenceAddressIsSameAsRegisteredOfficeAddress = correspondenceAddressIsSameAsRegisteredOfficeAddress;
    }

    public String getCorrespondenceEmail() {
        return correspondenceEmail;
    }

    public void setCorrespondenceEmail(String correspondenceEmail) {
        this.correspondenceEmail = correspondenceEmail;
    }
}
