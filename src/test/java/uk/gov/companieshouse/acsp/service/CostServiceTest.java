package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CostServiceTest {

    @InjectMocks
    private CostService costService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(costService, "costAmount", "20.00");
        ReflectionTestUtils.setField(costService, "acspPaymentTypes", Arrays.asList("debit-card", "credit-card"));
    }
    @Test
    void getCosts() {
        var result = costService.getCosts();

        assertEquals("20.00", result.getAmount());
        assertEquals(Arrays.asList("debit-card", "credit-card"), result.getAvailablePaymentMethods());
        assertEquals(Collections.singletonList("data-maintenance"), result.getClassOfPayment());
    }
}
