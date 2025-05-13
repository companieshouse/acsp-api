package uk.gov.companieshouse.acsp.models.enums;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class BusinessSectorTest {
    @Test
    void returnsCorrectLabelForAip() {
        assertEquals("auditors, insolvency-practitioners, external-accountants-and-tax-advisers", BusinessSector.AIP.getLabel());
    }

    @Test
    void returnsCorrectLabelForIlp() {
        assertEquals("independent-legal-professionals", BusinessSector.ILP.getLabel());
    }

    @Test
    void findsEnumByLabelSuccessfully() {
        assertEquals(BusinessSector.AIP, BusinessSector.findByLabel("auditors, insolvency-practitioners, external-accountants-and-tax-advisers"));
        assertEquals(BusinessSector.ILP, BusinessSector.findByLabel("independent-legal-professionals"));
        assertEquals(BusinessSector.TCSP, BusinessSector.findByLabel("trust-or-company-service-providers"));
    }

    @Test
    void returnsNullWhenLabelDoesNotMatchAnyBusinessSector() {
        assertNull(BusinessSector.findByLabel("non-existent-label"));
    }

    @Test
    void throwsExceptionWhenLabelIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BusinessSector.findByLabel(null);
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BusinessSector.findByLabel("");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelContainsOnlyWhitespace() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            BusinessSector.findByLabel("   ");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

}
