package uk.gov.companieshouse.acsp.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.filing.Filing;
import uk.gov.companieshouse.acsp.service.FilingsService;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingsControllerTest {

    private static final String ACSP_ID = "abc123";
    private static final String TRANSACTION_ID = "def456";
    private static final String PASS_THROUGH_HEADER = "545345345";

    @Mock
    private FilingsService filingsService;

    @InjectMocks
    private FilingsController filingsController;

    private MockHttpServletRequest mockHttpServletRequest;

    @BeforeEach
    void init() {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);
        mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("ERIC-Access-Token", PASS_THROUGH_HEADER);
    }

    @Test
    void testGetFilingReturnsSuccessfully() throws ServiceException, SubmissionNotLinkedToTransactionException {
        Filing filing = new Filing();
        FilingApi filingApi = new FilingApi();
        filingApi.setDescription("12345678");
        filing.setFilingItems(new FilingApi[] {filingApi});
        when(filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER)).thenReturn(filing);
        var result = filingsController.getFiling(ACSP_ID, TRANSACTION_ID, mockHttpServletRequest);
        Assertions.assertNotNull(result.getBody());
        Assertions.assertEquals(1, result.getBody().getFilingItems().length);
        Assertions.assertEquals("12345678", result.getBody().getFilingItems()[0].getDescription());
    }

    @Test
    void testGetFilingSubmissionNotFound() throws ServiceException , SubmissionNotLinkedToTransactionException {
        when(filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER)).thenThrow(ServiceException.class);
        var result = filingsController.getFiling(ACSP_ID, TRANSACTION_ID, mockHttpServletRequest);
        Assertions.assertNull(result.getBody());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    void testGetFilingSubmissionWhenUnexpectedException() throws ServiceException, SubmissionNotLinkedToTransactionException {
        when(filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER)).thenThrow(NullPointerException.class);
        var result = filingsController.getFiling(ACSP_ID, TRANSACTION_ID, mockHttpServletRequest);
        Assertions.assertNull(result.getBody());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }
}
