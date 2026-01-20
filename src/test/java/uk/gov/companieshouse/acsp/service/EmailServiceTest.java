package uk.gov.companieshouse.acsp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.client.EmailClient;
import uk.gov.companieshouse.acsp.exception.EmailSendException;
import uk.gov.companieshouse.acsp.factory.SendEmailFactory;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.acsp.models.enums.ApplicationType;
import uk.gov.companieshouse.api.chskafka.SendEmail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    EmailClient emailClient;

    @Mock
    SendEmailFactory sendEmailFactory;

    @InjectMocks
    EmailService emailService;

    private ClientVerificationEmailData emailData;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        emailData = new ClientVerificationEmailData();
        emailData.setClientName("Client Name");
        emailData.setReferenceNumber("12345");
        emailData.setClientEmailAddress("client@example.com");
        emailData.setTo("recipient@example.com");
        Mockito.lenient().when(sendEmailFactory.createSendEmail(any(), any())).thenReturn(mock(SendEmail.class));
    }

    @Test
    void sendClientVerificationEmailSuccess() throws EmailSendException {
        emailService.sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress(),
                ApplicationType.VERIFICATION);

        verify(emailClient).sendEmail(any(SendEmail.class));
    }

    @Test
    void sendClientVerificationEmailFailure() throws EmailSendException {
        doThrow(EmailSendException.class).when(emailClient).sendEmail(any(SendEmail.class));

        String to = emailData.getTo();
        String clientName = emailData.getClientName();
        String referenceNumber = emailData.getReferenceNumber();
        String clientEmailAddress = emailData.getClientEmailAddress();

        assertThrows(EmailSendException.class, () -> emailService.sendClientVerificationEmail(
                to,
                clientName,
                referenceNumber,
                clientEmailAddress,
                ApplicationType.VERIFICATION));
    }

    @Test
    void sendClientReverificationEmailSuccess() throws EmailSendException {
        emailService.sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress(),
                ApplicationType.REVERIFICATION);

        verify(emailClient).sendEmail(any(SendEmail.class));
    }

    @Test
    void sendClientReverificationEmailFailure() throws EmailSendException {
        doThrow(EmailSendException.class).when(emailClient).sendEmail(any(SendEmail.class));

        String to = emailData.getTo();
        String clientName = emailData.getClientName();
        String referenceNumber = emailData.getReferenceNumber();
        String clientEmailAddress = emailData.getClientEmailAddress();

        assertThrows(EmailSendException.class, () -> emailService.sendClientVerificationEmail(
                to,
                clientName,
                referenceNumber,
                clientEmailAddress,
                ApplicationType.REVERIFICATION));
    }

    @Test
    void sendClientVerificationEmailJsonProcessingException() throws JsonProcessingException {
        // Arrange
        String to = emailData.getTo();
        String clientName = emailData.getClientName();
        String referenceNumber = emailData.getReferenceNumber();
        String clientEmailAddress = emailData.getClientEmailAddress();
        ApplicationType applicationType = ApplicationType.VERIFICATION;

        Mockito.doThrow(JsonProcessingException.class).when(sendEmailFactory).createSendEmail(any(), any());

        // Act & Assert
        EmailSendException exception = assertThrows(EmailSendException.class, () ->
                emailService.sendClientVerificationEmail(to, clientName, referenceNumber, clientEmailAddress, applicationType)
        );

        // Verify
        verify(sendEmailFactory).createSendEmail(any(), any());
        assertEquals("Error encoding email data: N/A", exception.getMessage());
    }
}
