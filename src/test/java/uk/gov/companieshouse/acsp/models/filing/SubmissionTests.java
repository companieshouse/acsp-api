package uk.gov.companieshouse.acsp.models.filing;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class SubmissionTests {

    @Test
    void correctlySetsAndGetsReceivedAt() {
        Submission submission = new Submission();
        LocalDateTime now = LocalDateTime.now();
        submission.setReceivedAt(now);
        assertEquals(now, submission.getReceivedAt());
    }

    @Test
    void correctlySetsAndGetsTransactionId() {
        Submission submission = new Submission();
        submission.setTransactionId("txn-123");
        assertEquals("txn-123", submission.getTransactionId());
    }

    @Test
    void correctlySetsAndGetsCompanyNumber() {
        Submission submission = new Submission();
        submission.setCompanyNumber("12345678");
        assertEquals("12345678", submission.getCompanyNumber());
    }

    @Test
    void correctlySetsAndGetsCompanyName() {
        Submission submission = new Submission();
        submission.setCompanyName("Test Company Ltd");
        assertEquals("Test Company Ltd", submission.getCompanyName());
    }

    @Test
    void returnsNullForUnsetFields() {
        Submission submission = new Submission();
        assertNull(submission.getReceivedAt());
        assertNull(submission.getTransactionId());
        assertNull(submission.getCompanyNumber());
        assertNull(submission.getCompanyName());
    }
}