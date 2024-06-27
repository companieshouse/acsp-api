package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PersonName {

    @JsonProperty("forename")
    private String firstName;
    @JsonProperty("surname")
    private String lastName;
    @JsonProperty("other_forenames")
    private String middleName;

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


}
