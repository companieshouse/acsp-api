package uk.gov.companieshouse.acsp.models.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SectorOfWorkDaoTests {

    @Test
    void correctlySetsAndGetsWhichSector() {
        SectorOfWorkDao sector = new SectorOfWorkDao();
        sector.setWhichSector("Technology");
        assertEquals("Technology", sector.getWhichSector());
    }

    @Test
    void returnsNullForUnsetWhichSector() {
        SectorOfWorkDao sector = new SectorOfWorkDao();
        assertNull(sector.getWhichSector());
    }
}