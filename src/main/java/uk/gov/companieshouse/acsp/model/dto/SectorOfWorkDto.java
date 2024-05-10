package uk.gov.companieshouse.acsp.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SectorOfWorkDto {

    @JsonProperty("whichSector")
    private String whichSector;

    public String getWhichSector() {
        return whichSector;
    }

    public void setWhichSector(String whichSector) {
        this.whichSector = whichSector;
    }
}
