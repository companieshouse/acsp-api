package uk.gov.companieshouse.acsp.models.enums;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class AMLSupervisoryBodiesTest {
    @Test
    void returnsCorrectLabelForAAT() {
        assertEquals("association-of-accounting-technicians", AMLSupervisoryBodies.AAT.getLabel());
    }

    @Test
    void returnsCorrectLabelForICAEW() {
        assertEquals("institute-of-chartered-accountants-in-england-and-wales", AMLSupervisoryBodies.ICAEW.getLabel());
    }

    @Test
    void findsEnumByLabelSuccessfully() {
        assertEquals(AMLSupervisoryBodies.ACCA, AMLSupervisoryBodies.findByLabel("association-of-chartered-certified-accountants"));
        assertEquals(AMLSupervisoryBodies.FCA, AMLSupervisoryBodies.findByLabel("financial-conduct-authority"));
        assertEquals(AMLSupervisoryBodies.HMRC, AMLSupervisoryBodies.findByLabel("her-majesty's-revenue-and-customs"));
    }

    @Test
    void returnsNullWhenLabelDoesNotMatchAnySupervisoryBody() {
        assertNull(AMLSupervisoryBodies.findByLabel("non-existent-label"));
    }

    @Test
    void returnsNullWhenLabelIsNull() {
        assertNull(AMLSupervisoryBodies.findByLabel(null));
    }

    @Test
    void throwsExceptionWhenLabelIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AMLSupervisoryBodies.findByLabel("");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelContainsOnlyWhitespace() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            AMLSupervisoryBodies.findByLabel("   ");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }
}
