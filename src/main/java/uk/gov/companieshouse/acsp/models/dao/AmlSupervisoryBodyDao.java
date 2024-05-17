package uk.gov.companieshouse.acsp.models.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class AmlSupervisoryBodyDao {

    @Field("aml_supervisory_body")
    private String amlSupervisoryBody;

    @Field("membership_Number")
    private String membershipNumber;

    public String getAmlSupervisoryBody() {
        return amlSupervisoryBody;
    }

    public void setAmlSupervisoryBody(String amlSupervisoryBody) {
        this.amlSupervisoryBody = amlSupervisoryBody;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public void setMembershipNumber(String membershipNumber) {
        this.membershipNumber = membershipNumber;
    }
}
