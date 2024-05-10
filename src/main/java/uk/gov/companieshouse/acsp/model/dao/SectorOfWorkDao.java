package uk.gov.companieshouse.acsp.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class SectorOfWorkDao {

    @Field("whichSector")
    private String whichSector;

    public String getWhichSector() {
        return whichSector;
    }

    public void setWhichSector(String whichSector) {
        this.whichSector = whichSector;
    }
}
