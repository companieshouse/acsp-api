package uk.gov.companieshouse.acsp.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SectorOfWorkDtoTests {

    @Test
    void correctlySetsAndGetsWhichSector() {
        SectorOfWorkDto sector = new SectorOfWorkDto();
        sector.setWhichSector("Healthcare");
        assertEquals("Healthcare", sector.getWhichSector());
    }

    @Test
    void returnsNullForUnsetWhichSector() {
        SectorOfWorkDto sector = new SectorOfWorkDto();
        assertNull(sector.getWhichSector());
    }
}