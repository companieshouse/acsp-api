package uk.gov.companieshouse.acsp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NationalityDto {

    @JsonProperty("first_name")
    private String firstNationality;

    @JsonProperty("second_nationality")
    private String secondNationality;

    @JsonProperty("third_nationality")
    private String thirdNationality;

    public String getFirstNationality() {
        return firstNationality;
    }

    public void setFirstNationality(String firstNationality) {
        this.firstNationality = firstNationality;
    }

    public String getSecondNationality() {
        return secondNationality;
    }

    public void setSecondNationality(String secondNationality) {
        this.secondNationality = secondNationality;
    }

    public String getThirdNationality() {
        return thirdNationality;
    }

    public void setThirdNationality(String thirdNationality) {
        this.thirdNationality = thirdNationality;
    }
}
