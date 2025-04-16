package uk.gov.companieshouse.acsp.models.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NationalityDaoTests {

    @Test
    void correctlySetsAndGetsFirstNationality() {
        NationalityDao nationality = new NationalityDao();
        nationality.setFirstNationality("British");
        assertEquals("British", nationality.getFirstNationality());
    }

    @Test
    void correctlySetsAndGetsSecondNationality() {
        NationalityDao nationality = new NationalityDao();
        nationality.setSecondNationality("French");
        assertEquals("French", nationality.getSecondNationality());
    }

    @Test
    void correctlySetsAndGetsThirdNationality() {
        NationalityDao nationality = new NationalityDao();
        nationality.setThirdNationality("German");
        assertEquals("German", nationality.getThirdNationality());
    }

    @Test
    void returnsNullForUnsetNationalities() {
        NationalityDao nationality = new NationalityDao();
        assertNull(nationality.getFirstNationality());
        assertNull(nationality.getSecondNationality());
        assertNull(nationality.getThirdNationality());
    }
}