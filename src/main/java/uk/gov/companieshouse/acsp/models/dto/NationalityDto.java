package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NationalityDto {

    private String firstNationality;

    private String secondNationality;

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