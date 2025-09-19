package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.email_producer.EmailProducer;
import uk.gov.companieshouse.email_producer.EmailSendingException;
import uk.gov.companieshouse.email_producer.model.EmailData;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    EmailProducer emailProducer;

    @InjectMocks
    EmailService emailService;

    private ClientVerificationEmailData emailData;

    @BeforeEach
    void setUp() {
        emailData = new ClientVerificationEmailData();
        emailData.setClientName("Client Name");
        emailData.setReferenceNumber("12345");
        emailData.setClientEmailAddress("client@example.com");
        emailData.setTo("recipient@example.com");
    }

    @Test
    void sendClientVerificationEmailSuccess() throws EmailSendingException {
        emailService.sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress());

        verify(emailProducer).sendEmail(
                argThat(emailData -> "Identity verified for client: Client Name".equals(emailData.getSubject())),
                eq("acsp_client_verification_email"));
    }

    @Test
    void sendClientVerificationEmailFailure() throws EmailSendingException {
        doThrow(EmailSendingException.class).when(emailProducer).sendEmail(any(EmailData.class), eq("acsp_client_verification_email"));
        assertThrows(EmailSendingException.class, () -> emailService.sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress()));
    }

    @Test
    void sendClientReverificationEmailSuccess() throws EmailSendingException {
        emailService.sendClientReverificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress());

        verify(emailProducer).sendEmail(
                argThat(emailData -> "Identity reverified for client: Client Name".equals(emailData.getSubject())),
                eq("acsp_client_reverification_email"));
    }

    @Test
    void sendClientReverificationEmailFailure() throws EmailSendingException {
        doThrow(EmailSendingException.class).when(emailProducer).sendEmail(any(EmailData.class), eq("acsp_client_reverification_email"));
        assertThrows(EmailSendingException.class, () -> emailService.sendClientReverificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress()));
    }
}
