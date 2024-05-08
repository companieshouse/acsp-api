package uk.gov.companieshouse.acsp.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class NationalityDao {

    @Field("first_name")
    private String firstNationality;

    @Field("second_nationality")
    private String secondNationality;

    @Field("third_nationality")
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
