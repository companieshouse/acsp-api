package uk.gov.companieshouse.acsp.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SectorOfWorkDto {

    private String whichSector;

    public String getWhichSector() {
        return whichSector;
    }

    public void setWhichSector(String whichSector) {
        this.whichSector = whichSector;
    }
}
