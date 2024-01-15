package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;
    @Mock
    private HttpServletRequest request;

    @Mock
    private TransactionService transactionService;
    private static final String TRANSACTION_ID = "GFEDCBA";
    private static final String PASSTHROUGH_HEADER = "passthrough";

    @Test
    void getTransaction() throws IOException, URIValidationException, ServiceException {
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(TRANSACTION_ID);
        when(transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID)).thenReturn(dummyTransaction);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = transactionController.getTransaction(TRANSACTION_ID, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTransactionServiceException() throws IOException, URIValidationException, ServiceException {
        doThrow(new ServiceException("ERROR", new IOException())).when(transactionService)
                .getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = transactionController.getTransaction(TRANSACTION_ID, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void updateTransaction() throws IOException, URIValidationException, ServiceException {
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(TRANSACTION_ID);
        when(transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID)).thenReturn(dummyTransaction);
        doNothing().when(transactionService).updateTransaction(PASSTHROUGH_HEADER, dummyTransaction);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = transactionController.patchTransaction(TRANSACTION_ID, request);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    void updateTransactionServiceException() throws IOException, URIValidationException, ServiceException {
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(TRANSACTION_ID);
        when(transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID)).thenReturn(dummyTransaction);
        doThrow(new ServiceException("ERROR", new IOException())).when(transactionService)
                .updateTransaction(PASSTHROUGH_HEADER, dummyTransaction);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = transactionController.patchTransaction(TRANSACTION_ID, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void closeTransaction() throws IOException, URIValidationException, ServiceException {
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(TRANSACTION_ID);
        when(transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID)).thenReturn(dummyTransaction);
        when(transactionService.closeTransaction(dummyTransaction)).thenReturn(true);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = transactionController.closeTransaction(TRANSACTION_ID, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void closeTransactionServiceException() throws IOException, URIValidationException, ServiceException {
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(TRANSACTION_ID);
        when(transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID)).thenReturn(dummyTransaction);
        doThrow(new ServiceException("ERROR", new IOException())).when(transactionService)
                .closeTransaction(dummyTransaction);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = transactionController.closeTransaction(TRANSACTION_ID, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
