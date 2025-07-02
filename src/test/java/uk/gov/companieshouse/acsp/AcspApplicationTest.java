package uk.gov.companieshouse.acsp;

import org.junit.jupiter.api.Test;
import java.util.TimeZone;
import static org.junit.jupiter.api.Assertions.*;

class AcspApplicationTest {

    @Test
    void initSetsDefaultTimeZoneToUTC() {
        var original = TimeZone.getDefault();
        try {
            var app = new AcspApplication();
            app.init();
            assertEquals(TimeZone.getTimeZone("UTC"), TimeZone.getDefault());
        } finally {
            TimeZone.setDefault(original);
        }
    }
}