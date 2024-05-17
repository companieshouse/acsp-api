package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NationalityDto {

    @JsonProperty("firstNationality")
    private String firstNationality;

    @JsonProperty("secondNationality")
    private String secondNationality;

    @JsonProperty("thirdNationality")
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
