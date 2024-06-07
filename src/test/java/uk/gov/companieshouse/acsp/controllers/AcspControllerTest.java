package uk.gov.companieshouse.acsp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.service.AcspService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcspControllerTest {

    private static final ResponseEntity<Object> CREATED_SUCCESS_RESPONSE = ResponseEntity.created(URI.create("URI")).body("Created");

    private static final String TRANSACTION_ID = "324234-123123-768685";
    private static final String REQUEST_ID = "fd4gld5h3jhh";
    private static final String USER_ID = "22334455";
    private static final String SUBMISSION_ID = "demo@ch.gov.uk";

    @Mock
    private AcspService acspService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private Transaction transaction;

    @InjectMocks
    private AcspController acspController;

    private AcspDataDto acspDataDto;

    @Test
    void createAcsp() throws ServiceException {
        when(transactionService.getTransaction(any(), any())).thenReturn(transaction);
        when(acspService.createAcspRegData(transaction,
                acspDataDto,
                REQUEST_ID,
                USER_ID)).thenReturn(CREATED_SUCCESS_RESPONSE);

        var response = acspController.createAcspData( TRANSACTION_ID,
                REQUEST_ID,
                USER_ID,
                acspDataDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createAcspTransactionError() throws ServiceException {
        ServiceException serviceException = new ServiceException("Could not find transaction");
        when(transactionService.getTransaction(any(), any())).thenThrow(serviceException);

        var response = acspController.createAcspData( TRANSACTION_ID,
                REQUEST_ID,
                USER_ID,
                acspDataDto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void saveAcsp() throws ServiceException {
        when(transactionService.getTransaction(any(), any())).thenReturn(transaction);
        when(acspService.saveAcspRegData(transaction,
                acspDataDto,
                REQUEST_ID,
                USER_ID)).thenReturn(CREATED_SUCCESS_RESPONSE);

        var response = acspController.saveAcspData( TRANSACTION_ID,
                REQUEST_ID,
                USER_ID,
                acspDataDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getAcsp() throws ServiceException, SubmissionNotLinkedToTransactionException {
        acspDataDto = new AcspDataDto();
        when(transactionService.getTransaction(any(), any())).thenReturn(transaction);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));

        var response = acspController.getAcspData(TRANSACTION_ID, SUBMISSION_ID, REQUEST_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getAcspWhenGetAcspReturnsNull() throws ServiceException, SubmissionNotLinkedToTransactionException {
        when(transactionService.getTransaction(any(), any())).thenReturn(transaction);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.empty());

        var response = acspController.getAcspData(TRANSACTION_ID, SUBMISSION_ID, REQUEST_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAcspWhenGetAcspThrowsException() throws ServiceException, SubmissionNotLinkedToTransactionException {
        acspDataDto = new AcspDataDto();
        when(transactionService.getTransaction(any(), any())).thenReturn(transaction);
        when(acspService.getAcsp(any(), any())).thenThrow(SubmissionNotLinkedToTransactionException.class);

        var response = acspController.getAcspData(TRANSACTION_ID, SUBMISSION_ID, REQUEST_ID);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void checkHasApplicationTrue() {
        when(acspService.getAcspApplicationCount(any())).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        var response = acspController.checkHasApplication(USER_ID, REQUEST_ID);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void checkHasApplicationFalse() {
        when(acspService.getAcspApplicationCount(any())).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        var response = acspController.checkHasApplication(USER_ID, REQUEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}