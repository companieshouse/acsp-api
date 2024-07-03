package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;

public class AMLSupervisoryBodiesDto {

    @JsonProperty("amlSupervisoryBody")
    private String amlSupervisoryBody;

    @JsonProperty("membershipId")
    private String membershipId;

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
