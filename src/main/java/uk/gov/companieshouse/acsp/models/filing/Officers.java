package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Officers {
    @JsonProperty("person_name")
    private PersonName personName;
    @JsonProperty("birthDate")
    private String birthDate;
    @JsonProperty("usualResidence")
    private String usualResidence;
    @JsonProperty("nationalityOther")
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
