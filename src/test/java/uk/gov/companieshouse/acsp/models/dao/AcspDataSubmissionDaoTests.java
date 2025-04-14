package uk.gov.companieshouse.acsp.models.dao;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class AcspDataSubmissionDaoTests {

    @Test
    void correctlySetsAndGetsCreatedAt() {
        AcspDataSubmissionDao dao = new AcspDataSubmissionDao();
        LocalDateTime now = LocalDateTime.now();
        dao.setCreatedAt(now);
        assertEquals(now, dao.getCreatedAt());
    }

    @Test
    void correctlySetsAndGetsUpdatedAt() {
        AcspDataSubmissionDao dao = new AcspDataSubmissionDao();
        LocalDateTime now = LocalDateTime.now();
        dao.setUpdatedAt(now);
        assertEquals(now, dao.getUpdatedAt());
    }

    @Test
    void correctlySetsAndGetsLastModifiedByUserId() {
        AcspDataSubmissionDao dao = new AcspDataSubmissionDao();
        dao.setLastModifiedByUserId("user123");
        assertEquals("user123", dao.getLastModifiedByUserId());
    }

    @Test
    void correctlySetsAndGetsHttpRequestId() {
        AcspDataSubmissionDao dao = new AcspDataSubmissionDao();
        dao.setHttpRequestId("req-456");
        assertEquals("req-456", dao.getHttpRequestId());
    }

    @Test
    void returnsNullForUnsetFields() {
        AcspDataSubmissionDao dao = new AcspDataSubmissionDao();
        assertNull(dao.getCreatedAt());
        assertNull(dao.getUpdatedAt());
        assertNull(dao.getLastModifiedByUserId());
        assertNull(dao.getHttpRequestId());
    }
}