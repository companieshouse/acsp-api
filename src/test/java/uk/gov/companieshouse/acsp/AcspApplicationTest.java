package uk.gov.companieshouse.acsp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.TimeZone;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AcspApplicationTest {

    private AcspApplication application;

    @BeforeEach
    void setup() {
        application = new AcspApplication();
    }

    @Test
    void setsDefaultTimeZoneToUTC() {
        application.init();
        assertEquals("UTC", TimeZone.getDefault().getID());
    }

    @Test
    void doesNotChangeTimeZoneIfAlreadyUTC() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        application.init();
        assertEquals("UTC", TimeZone.getDefault().getID());
    }

    @Test
    void changesTimeZoneFromNonUTCToUTC() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/London"));
        application.init();
        assertEquals("UTC", TimeZone.getDefault().getID());
    }
}