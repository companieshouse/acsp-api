package uk.gov.companieshouse.acsp.models.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class AcspDataSubmissionDtoTests {

    @Test
    void correctlySetsAndGetsCreatedAt() {
        AcspDataSubmissionDto dto = new AcspDataSubmissionDto();
        LocalDateTime now = LocalDateTime.now();
        dto.setCreatedAt(now);
        assertEquals(now, dto.getCreatedAt());
    }

    @Test
    void correctlySetsAndGetsUpdatedAt() {
        AcspDataSubmissionDto dto = new AcspDataSubmissionDto();
        LocalDateTime now = LocalDateTime.now();
        dto.setUpdatedAt(now);
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void correctlySetsAndGetsLastModifiedByUserId() {
        AcspDataSubmissionDto dto = new AcspDataSubmissionDto();
        dto.setLastModifiedByUserId("user123");
        assertEquals("user123", dto.getLastModifiedByUserId());
    }

    @Test
    void correctlySetsAndGetsHttpRequestId() {
        AcspDataSubmissionDto dto = new AcspDataSubmissionDto();
        dto.setHttpRequestId("req-456");
        assertEquals("req-456", dto.getHttpRequestId());
    }

    @Test
    void returnsNullForUnsetFields() {
        AcspDataSubmissionDto dto = new AcspDataSubmissionDto();
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
        assertNull(dto.getLastModifiedByUserId());
        assertNull(dto.getHttpRequestId());
    }
}