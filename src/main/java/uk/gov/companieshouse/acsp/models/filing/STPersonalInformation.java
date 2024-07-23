package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class STPersonalInformation {

    @JsonProperty("person_name")
    private PersonName personName;
    @JsonProperty("date_of_birth")
    private String birthDate;
    @JsonProperty("country_of_residence")
    private String usualResidence;
    @JsonProperty("nationalities")
    private String nationalityOther;

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

    public PersonName getPersonName() {
        return personName;
    }

    public void setPersonName(PersonName personName) {
        this.personName = personName;
    }
}
