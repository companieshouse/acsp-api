package uk.gov.companieshouse.acsp.models.filing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PresenterTests {

    @Test
    void correctlySetsAndGetsFirstName() {
        Presenter presenter = new Presenter();
        presenter.setFirstName("John");
        assertEquals("John", presenter.getFirstName());
    }

    @Test
    void correctlySetsAndGetsLastName() {
        Presenter presenter = new Presenter();
        presenter.setLastName("Doe");
        assertEquals("Doe", presenter.getLastName());
    }

    @Test
    void correctlySetsAndGetsUserId() {
        Presenter presenter = new Presenter();
        presenter.setUserId("user123");
        assertEquals("user123", presenter.getUserId());
    }

    @Test
    void correctlySetsAndGetsLanguage() {
        Presenter presenter = new Presenter();
        presenter.setLanguage("en");
        assertEquals("en", presenter.getLanguage());
    }

    @Test
    void returnsNullForUnsetFields() {
        Presenter presenter = new Presenter();
        assertNull(presenter.getFirstName());
        assertNull(presenter.getLastName());
        assertNull(presenter.getUserId());
        assertNull(presenter.getLanguage());
    }
}