package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AMLSupervisoryBodiesDto {

    private AMLSupervisoryBodies amlSupervisoryBody;

    private String membershipId;

    public AMLSupervisoryBodiesDto() {
    }

    public AMLSupervisoryBodiesDto(AMLSupervisoryBodies amlSupervisoryBody, String membershipId) {
        this.amlSupervisoryBody = amlSupervisoryBody;
        this.membershipId = membershipId;
    }

    public AMLSupervisoryBodies getAmlSupervisoryBody() {
        return amlSupervisoryBody;
    }

    public void setAmlSupervisoryBody(AMLSupervisoryBodies amlSupervisoryBody) {
        this.amlSupervisoryBody = amlSupervisoryBody;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }
}
