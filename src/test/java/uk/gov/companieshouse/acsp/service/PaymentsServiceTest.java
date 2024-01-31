package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.reflect.Whitebox;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.payment.PaymentResourceHandler;
import uk.gov.companieshouse.api.handler.payment.request.PaymentCreate;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.payment.PaymentSessionApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;
import uk.gov.companieshouse.environment.EnvironmentReader;

import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentsServiceTest {
    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private PaymentResourceHandler paymentResourceHandler;

    @Mock
    private PaymentCreate paymentCreate;

    @Mock
    private ApiResponse<PaymentApi> response;

    private  EnvironmentReader environmentReader;
    @InjectMocks
    private PaymentsService paymentsService;

    private static final String TRANSACTION_ID = "12345678";

    private Transaction transaction;

    @BeforeEach
    void setUp(){
        transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);
        TransactionLinks transactionLinks = new TransactionLinks();
        transactionLinks.setPayment("/payments");
        transaction.setLinks(transactionLinks);
        environmentReader = Mockito.mock(EnvironmentReader.class);
        Mockito.when(environmentReader.getMandatoryString(Mockito.anyString())).thenReturn("http://chs.local");
        Whitebox.setInternalState(PaymentsService.class, "environmentReader", environmentReader);

    }
    @Test
    void getPaymentDetails_Success() throws IOException, URIValidationException, ServiceException {
        PaymentApi paymentApi = new PaymentApi();
        paymentApi.setAmount("100");

        when(apiClientService.getApiClient(null)).thenReturn(apiClient);
        when(apiClient.payment()).thenReturn(paymentResourceHandler);
        when(paymentResourceHandler.create(anyString(), any(PaymentSessionApi.class))).thenReturn(paymentCreate);
        when(paymentCreate.execute()).thenReturn(response);
        when(response.getData()).thenReturn(paymentApi);

        assertEquals(paymentApi,paymentsService.createPaymentSession(null, transaction));
    }

    @Test
    void getPaymentDetails_IOException() throws IOException {

        when(apiClientService.getApiClient(null)).thenThrow(new IOException("ERROR"));
        assertThrows(RuntimeException.class,() -> paymentsService.createPaymentSession(null, transaction));
    }

    @Test
    void getPaymentDetails_URIValidationException() throws IOException, URIValidationException {

        when(apiClientService.getApiClient(null)).thenReturn(apiClient);
        when(apiClient.payment()).thenReturn(paymentResourceHandler);
        when(paymentResourceHandler.create(anyString(), any(PaymentSessionApi.class))).thenReturn(paymentCreate);
        when(paymentCreate.execute()).thenThrow(new URIValidationException("ERROR"));

        assertThrows(RuntimeException.class, () -> {
            paymentsService.createPaymentSession(null, transaction);
        });
    }

    @Test
    void getPaymentDetails_ApiErrorResponseException() throws IOException, URIValidationException {

        when(apiClientService.getApiClient(null)).thenReturn(apiClient);
        when(apiClient.payment()).thenReturn(paymentResourceHandler);
        when(paymentResourceHandler.create(anyString(), any(PaymentSessionApi.class))).thenReturn(paymentCreate);
        when(paymentCreate.execute()).thenThrow(ApiErrorResponseException.fromIOException(new IOException("ERROR")));

        assertThrows(RuntimeException.class, () -> paymentsService.createPaymentSession(null, transaction));
    }
}