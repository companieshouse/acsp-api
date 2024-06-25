package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Officers {
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("birthDate")
    private String birthDate;
    @JsonProperty("usualResidence")
    private String usualResidence;
    @JsonProperty("nationalityOther")
    private String nationalityOther;

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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUsualResidence() {
        return usualResidence;
    }

    public void setUsualResidence(String usualResidence) {
        this.usualResidence = usualResidence;
    }

    public String getNationalityOther() {
        return nationalityOther;
    }

    public void setNationalityOther(String nationalityOther) {
        this.nationalityOther = nationalityOther;
    }
}
