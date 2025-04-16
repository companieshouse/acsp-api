package uk.gov.companieshouse.acsp.models.enums;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

public class RoleTypeTest {
    @Test
    void returnsCorrectLabelForSoleTrader() {
        assertEquals("sole-trader", RoleType.SOLE_TRADER.getLabel());
    }

    @Test
    void returnsCorrectLabelForDirector() {
        assertEquals("director", RoleType.DIRECTOR.getLabel());
    }

    @Test
    void findsEnumByLabelSuccessfully() {
        assertEquals(RoleType.SOLE_TRADER, RoleType.findByLabel("sole-trader"));
        assertEquals(RoleType.DIRECTOR, RoleType.findByLabel("director"));
        assertEquals(RoleType.MEMBER_OF_PARTNERSHIP, RoleType.findByLabel("member-of-partnership"));
    }

    @Test
    void returnsNullWhenLabelDoesNotMatchAnyRoleType() {
        assertNull(RoleType.findByLabel("non-existent-label"));
    }

    @Test
    void throwsExceptionWhenLabelIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            RoleType.findByLabel(null);
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelIsEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            RoleType.findByLabel("");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenLabelContainsOnlyWhitespace() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            RoleType.findByLabel("   ");
        });
        assertEquals("Label cannot be empty", exception.getMessage());
    }
}
