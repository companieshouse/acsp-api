package uk.gov.companieshouse.acsp.models.filing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PresenterTest {

    @Test
    void gettersAndSettersWorkAsExpected() {
        Presenter presenter = new Presenter();
        presenter.setFirstName("Alice");
        presenter.setLastName("Smith");
        presenter.setUserId("user-123");
        presenter.setLanguage("en");

        assertEquals("Alice", presenter.getFirstName());
        assertEquals("Smith", presenter.getLastName());
        assertEquals("user-123", presenter.getUserId());
        assertEquals("en", presenter.getLanguage());
    }
}
