package uk.gov.companieshouse.acsp.models.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;

public class AMLSupervisoryBodiesDao {
    @JsonProperty("aml_supervisory_body")
//    private AMLSupervisoryBodies amlSupervisoryBody;   //this needs to be fixed in web
    private String amlSupervisoryBody;

    @JsonProperty("membership_id")
    private String membershipId;

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

//    public AMLSupervisoryBodies getAmlSupervisoryBody() {
//        return amlSupervisoryBody;
//    }
//
//    public void setAmlSupervisoryBody(AMLSupervisoryBodies amlSupervisoryBody) {
//        this.amlSupervisoryBody = amlSupervisoryBody;
//    }

    public String getAmlSupervisoryBody() {
        return amlSupervisoryBody;
    }

    public void setAmlSupervisoryBody(String amlSupervisoryBody) {
        this.amlSupervisoryBody = amlSupervisoryBody;
    }
}
