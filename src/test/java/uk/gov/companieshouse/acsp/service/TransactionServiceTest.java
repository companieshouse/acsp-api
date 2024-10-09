package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.privatetransaction.PrivateTransactionResourceHandler;
import uk.gov.companieshouse.api.handler.privatetransaction.request.PrivateTransactionPatch;
import uk.gov.companieshouse.api.handler.transaction.TransactionsResourceHandler;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsDelete;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsGet;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsUpdate;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.acsp.util.Constants.PAYMENT_REQUIRED_HEADER;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    InternalApiClient internalApiClient;

    @Mock
    private TransactionsResourceHandler transactionsResourceHandler;

    @Mock
    private PrivateTransactionResourceHandler privateTransactionResourceHandler;

    @Mock
    private TransactionsGet transactionsGet;

    @Mock
    private TransactionsUpdate transactionsUpdate;

    @Mock
    private PrivateTransactionPatch privateTransactionPatch;

    @Mock
    private TransactionsDelete transactionsDelete;

    @Mock
    private ApiResponse<Transaction> apiResponse;

    @Mock
    private ApiResponse<Void> responseWithoutData;

    @Mock
    private Map<String, Object> headers;

    @InjectMocks
    private TransactionService transactionService;

    private static final String TRANSACTION_ID = "12345678";
    private static final String PASSTHROUGH_HEADER = "passthrough";
    private static final String PAYMENT_URL = "paymentUrl";
    @Test
    void getTransactionSuccess() throws IOException, URIValidationException, ServiceException {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.get("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsGet);
        when(transactionsGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(transaction);

        var response = transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID);

        assertEquals(transaction, response);

    }

    @Test
    void getTransactionURIValidationException() throws IOException, URIValidationException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.get("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsGet);
        when(transactionsGet.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> {
            transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID);
        });
    }

    @Test
    void getTransactionProfileApiErrorResponse() throws IOException, URIValidationException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.get("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsGet);
        when(transactionsGet.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> {
            transactionService.getTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID);
        });
    }

    @Test
    void updateTransactionSuccess() throws IOException, URIValidationException{
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getInternalApiClient(PASSTHROUGH_HEADER)).thenReturn(internalApiClient);
        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch("/private/transactions/" + TRANSACTION_ID, transaction)).thenReturn(privateTransactionPatch);
        when(privateTransactionPatch.execute()).thenReturn(new ApiResponse<>(204, null));

        assertDoesNotThrow(() -> transactionService.updateTransaction(PASSTHROUGH_HEADER, transaction));

        verify(privateTransactionPatch, times(1)).execute();
    }

    @Test
    void updateTransactionApiErrorResponse() throws IOException, URIValidationException {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getInternalApiClient(PASSTHROUGH_HEADER)).thenReturn(internalApiClient);
        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch("/private/transactions/" + TRANSACTION_ID, transaction)).thenReturn(privateTransactionPatch);
        when(privateTransactionPatch.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> transactionService.updateTransaction(PASSTHROUGH_HEADER, transaction));
    }

    @Test
    void updateTransactionUriValidationError() throws IOException, URIValidationException {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getInternalApiClient(PASSTHROUGH_HEADER)).thenReturn(internalApiClient);
        when(internalApiClient.privateTransaction()).thenReturn(privateTransactionResourceHandler);
        when(privateTransactionResourceHandler.patch("/private/transactions/" + TRANSACTION_ID, transaction)).thenReturn(privateTransactionPatch);
        when(privateTransactionPatch.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> transactionService.updateTransaction(PASSTHROUGH_HEADER, transaction));
    }

    @Test
    void closeTransactionSuccessForPayableTransaction() throws ServiceException, IOException, URIValidationException {

        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getApiClient()).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.update("/transactions/" + TRANSACTION_ID, transaction)).thenReturn(transactionsUpdate);
        when(transactionsUpdate.execute()).thenReturn(responseWithoutData);
        when(responseWithoutData.getHeaders()).thenReturn(headers);

        List<String> paymentRequiredHeader = new ArrayList<>();
        paymentRequiredHeader.add(PAYMENT_URL);
        when(headers.get(PAYMENT_REQUIRED_HEADER)).thenReturn(paymentRequiredHeader);

        boolean paymentRequired = transactionService.closeTransaction(transaction);

        assertTrue(paymentRequired);
    }

    @Test
    void closeTransactionSuccessForNonPayableTransaction() throws ServiceException, IOException, URIValidationException {

        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getApiClient()).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.update("/transactions/" + TRANSACTION_ID, transaction)).thenReturn(transactionsUpdate);
        when(transactionsUpdate.execute()).thenReturn(responseWithoutData);
        when(responseWithoutData.getHeaders()).thenReturn(headers);
        when(headers.get(PAYMENT_REQUIRED_HEADER)).thenReturn(null);

        boolean paymentRequired = transactionService.closeTransaction(transaction);

        assertFalse(paymentRequired);
    }

    @Test
    void closeTransactionUriValidationError() throws IOException, URIValidationException {

        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getApiClient()).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.update("/transactions/" + TRANSACTION_ID, transaction)).thenReturn(transactionsUpdate);
        when(transactionsUpdate.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> transactionService.closeTransaction(transaction));
    }

    @Test
    void closeTransactionUriApiErrorResponse() throws IOException, URIValidationException {

        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        when(apiClientService.getApiClient()).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.update("/transactions/" + TRANSACTION_ID, transaction)).thenReturn(transactionsUpdate);
        when(transactionsUpdate.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> transactionService.closeTransaction(transaction));
    }

    @Test
    void deleteTransactionSuccess() throws IOException, URIValidationException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.delete("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsDelete);
        when(transactionsDelete.execute()).thenReturn(new ApiResponse<>(204, null));

        assertDoesNotThrow(() -> transactionService.deleteTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID));

        verify(transactionsResourceHandler, times(1)).delete("/transactions/" + TRANSACTION_ID);
        verify(transactionsDelete, times(1)).execute();
    }

    @Test
    void deleteTransactionThrowsApiErrorResponseException() throws IOException, URIValidationException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.delete("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsDelete);
        when(transactionsDelete.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(ServiceException.class, () -> transactionService.deleteTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID));
    }

    @Test
    void deleteTransactionThrowsUriValidationException() throws IOException, URIValidationException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.delete("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsDelete);
        when(transactionsDelete.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(ServiceException.class, () -> transactionService.deleteTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID));
    }

    @Test
    void deleteTransactionInvalidStatusCode() throws IOException, URIValidationException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.delete("/transactions/" + TRANSACTION_ID)).thenReturn(transactionsDelete);
        when(transactionsDelete.execute()).thenReturn(new ApiResponse<>(500, null));

        assertThrows(ServiceException.class, () -> transactionService.deleteTransaction(PASSTHROUGH_HEADER, TRANSACTION_ID));
    }
}
