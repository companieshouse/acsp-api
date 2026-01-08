package uk.gov.companieshouse.acsp.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.exception.EmailSendException;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.PrivateSendEmailHandler;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateSendEmailPost;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.model.ApiResponse;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailClientTest {

    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateSendEmailHandler privateSendEmailHandler;

    @Mock
    private PrivateSendEmailPost privateSendEmailPost;

    @Mock
    private ApiResponse<Void> apiResponse;

    @Mock
    private HttpClient httpClient;

    private EmailClient emailClient;

    @BeforeEach
    void setUp() {
        emailClient = new EmailClient(internalApiClientSupplier);
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        when(internalApiClient.getHttpClient()).thenReturn(httpClient);
        when(internalApiClient.sendEmailHandler()).thenReturn(privateSendEmailHandler);
    }

    @Test
    void testSendEmail_Success() throws Exception {
        // Arrange
        SendEmail sendEmail = new SendEmail();
        sendEmail.setJsonData("test-email-data");

        when(privateSendEmailHandler.postSendEmail(anyString(), eq(sendEmail))).thenReturn(privateSendEmailPost);
        when(privateSendEmailPost.execute()).thenReturn(apiResponse);
        when(apiResponse.getStatusCode()).thenReturn(200);

        // Act & Assert
        assertDoesNotThrow(() -> emailClient.sendEmail(sendEmail));

        verify(internalApiClient).getHttpClient();
        verify(privateSendEmailPost).execute();
    }

    @Test
    void testSendEmail_ApiErrorResponseException() throws Exception {
        // Arrange
        SendEmail sendEmail = new SendEmail();
        sendEmail.setJsonData("test-email-data");
        when(privateSendEmailHandler.postSendEmail(anyString(), eq(sendEmail))).thenReturn(privateSendEmailPost);
        when(privateSendEmailPost.execute()).thenThrow(mock(ApiErrorResponseException.class));
        // Act & Assert
        assertThrows(EmailSendException.class, () -> emailClient.sendEmail(sendEmail));

        verify(privateSendEmailPost).execute();
    }
}