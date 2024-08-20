package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.LocalDate;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApplicantDetailsDto {

    private String firstName;

    private String lastName;

    private String middleName;

    private AddressDto correspondenceAddress;

    private LocalDate dateOfBirth;

    private NationalityDto nationality;

    private String countryOfResidence;

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

    public AddressDto getCorrespondenceAddress() {
        return correspondenceAddress;
    }

    public void setCorrespondenceAddress(AddressDto correspondenceAddress) {
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
