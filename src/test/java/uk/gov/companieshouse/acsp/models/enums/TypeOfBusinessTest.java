package uk.gov.companieshouse.acsp.models.enums;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class TypeOfBusinessTest {
    @Test
    void returnsCorrectLabelForLimitedCompany() {
        assertEquals("limited-company", TypeOfBusiness.LC.getLabel());
    }

    @Test
    void returnsCorrectLabelForLimitedPartnership() {
        assertEquals("limited-partnership", TypeOfBusiness.LP.getLabel());
    }

    @Test
    void findsEnumByLabelSuccessfully() {
        assertEquals(TypeOfBusiness.LC, TypeOfBusiness.findByLabel("limited-company"));
        assertEquals(TypeOfBusiness.LLP, TypeOfBusiness.findByLabel("limited-liability-partnership"));
        assertEquals(TypeOfBusiness.SOLE_TRADER, TypeOfBusiness.findByLabel("sole-trader"));
    }

    @Test
    void returnsNullWhenLabelDoesNotMatchAnyTypeOfBusiness() {
        assertNull(TypeOfBusiness.findByLabel("non-existent-label"));
    }

    @Test
    void throwsExceptionWhenLabelIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            TypeOfBusiness.findByLabel(null);
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            TypeOfBusiness.findByLabel("");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelContainsOnlyWhitespace() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            TypeOfBusiness.findByLabel("   ");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }
}
