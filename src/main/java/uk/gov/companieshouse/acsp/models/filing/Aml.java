package uk.gov.companieshouse.acsp.models.filing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Aml {

    @JsonProperty("aml_memberships")
    private AmlMembership[] amlMemberships;
    @JsonProperty("person_name")
    private PersonName personName;
    @JsonProperty("previous_aml_memberships")
    private AmlMembership[] previousAmlMemberships;

    public AmlMembership[] getAmlMemberships() {
        return amlMemberships;
    }

    public void setAmlMemberships(AmlMembership[] amlMemberships) {
        this.amlMemberships = amlMemberships;
    }

    public PersonName getPersonName() {
        return personName;
    }

    public void setPersonName(PersonName personName) {
        this.personName = personName;
    }

    public AmlMembership[] getPreviousAmlMemberships() { return previousAmlMemberships; }

    public void setPreviousAmlMemberships(AmlMembership[] previousAmlMemberships) {
        this.previousAmlMemberships = previousAmlMemberships;
    }
}
