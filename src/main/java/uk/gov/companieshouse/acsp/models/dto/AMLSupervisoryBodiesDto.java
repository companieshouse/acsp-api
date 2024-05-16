package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;

public class AMLSupervisoryBodiesDto {

    @JsonProperty("membershipId")
    private String membershipId;

    @JsonProperty("amlSupervisoryBody")
    private AMLSupervisoryBodies amlSupervisoryBody;

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }

    public AMLSupervisoryBodies getAmlSupervisoryBody() {
        return amlSupervisoryBody;
    }

    public void setAmlSupervisoryBody(AMLSupervisoryBodies amlSupervisoryBody) {
        this.amlSupervisoryBody = amlSupervisoryBody;
    }
}
