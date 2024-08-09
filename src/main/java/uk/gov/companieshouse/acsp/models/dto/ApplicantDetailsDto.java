package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.acsp.models.type.Address;

import java.time.LocalDate;

public class ApplicantDetailsDto {

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("middleName")
    private String middleName;

    @JsonProperty("correspondenceAddress")
    private Address correspondenceAddress;

    @JsonProperty("dateOfBirth")
    private LocalDate dateOfBirth;

    @JsonProperty("nationality")
    private NationalityDto nationality;

    @JsonProperty("countryOfResidence")
    private String countryOfResidence;

    @JsonProperty("correspondenceAddressIsSameAsRegisteredOfficeAddress")
    private boolean correspondenceAddressIsSameAsRegisteredOfficeAddress;


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

    public Address getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(Address correspondenceAddress) {
        this.correspondenceAddress = correspondenceAddress;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public boolean getCorrespondenceAddressIsSameAsRegisteredOfficeAddress() {
        return correspondenceAddressIsSameAsRegisteredOfficeAddress;
    }

    public void setCorrespondenceAddressIsSameAsRegisteredOfficeAddress(boolean correspondenceAddressIsSameAsRegisteredOfficeAddress) {
        this.correspondenceAddressIsSameAsRegisteredOfficeAddress = correspondenceAddressIsSameAsRegisteredOfficeAddress;
    }
}
