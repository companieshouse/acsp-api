package uk.gov.companieshouse.acsp.models.filing;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SubmissionTest {

    @Test
    void testGettersAndSetters() {
        Submission submission = new Submission();
        LocalDateTime now = LocalDateTime.now();
        submission.setReceivedAt(now);
        submission.setTransactionId("txn123");
        submission.setCompanyNumber("12345678");
        submission.setCompanyName("Test Company Ltd");

        assertEquals(now, submission.getReceivedAt());
        assertEquals("txn123", submission.getTransactionId());
        assertEquals("12345678", submission.getCompanyNumber());
        assertEquals("Test Company Ltd", submission.getCompanyName());
    }
}