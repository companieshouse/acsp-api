package uk.gov.companieshouse.acsp.models.enums;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AcspTypeTest {
    @Test
    void throwsExceptionWhenLabelIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AcspType.findByLabel("");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelContainsOnlyWhitespace() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AcspType.findByLabel("   ");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }
}
