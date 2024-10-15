package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AMLSupervisoryBodiesDto {

    private String amlSupervisoryBody;

    private String membershipId;

    private String amlAcronym;

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

    public String getAmlAcronym() {
        return amlAcronym;
    }

    public void setAmlAcronym(String amlAcronym) {
        this.amlAcronym = amlAcronym;
    }
}
