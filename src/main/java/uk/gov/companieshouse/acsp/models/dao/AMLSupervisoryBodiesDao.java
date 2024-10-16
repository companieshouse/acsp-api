package uk.gov.companieshouse.acsp.models.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AMLSupervisoryBodiesDao {
    @JsonProperty("aml_supervisory_body")
    private String amlSupervisoryBody;

    @JsonProperty("membership_id")
    private String membershipId;

    public AMLSupervisoryBodiesDao() {
    }

    public AMLSupervisoryBodiesDao(String amlSupervisoryBody, String membershipId) {
        this.amlSupervisoryBody = amlSupervisoryBody;
        this.membershipId = membershipId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public String getAmlSupervisoryBody() {
        return amlSupervisoryBody;
    }

    public void setAmlSupervisoryBody(String amlSupervisoryBody) {
        this.amlSupervisoryBody = amlSupervisoryBody;
    }
}
