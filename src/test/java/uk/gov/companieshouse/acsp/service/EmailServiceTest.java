package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.acsp.models.enums.ApplicationType;
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
class EmailServiceTest {

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
                emailData.getClientEmailAddress(),
                ApplicationType.VERIFICATION);

        verify(emailProducer).sendEmail(
                argThat(sentEmailData -> "Identity verified for client: Client Name".equals(sentEmailData.getSubject())),
                eq("acsp_client_verification_email"));
    }

    @Test
    void sendClientVerificationEmailFailure() throws EmailSendingException {
        doThrow(EmailSendingException.class).when(emailProducer).sendEmail(any(EmailData.class), eq("acsp_client_verification_email"));

        String to = emailData.getTo();
        String clientName = emailData.getClientName();
        String referenceNumber = emailData.getReferenceNumber();
        String clientEmailAddress = emailData.getClientEmailAddress();

        assertThrows(EmailSendingException.class, () -> emailService.sendClientVerificationEmail(
                to,
                clientName,
                referenceNumber,
                clientEmailAddress,
                ApplicationType.VERIFICATION));
    }

    @Test
    void sendClientReverificationEmailSuccess() throws EmailSendingException {
        emailService.sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress(),
                ApplicationType.REVERIFICATION);

        verify(emailProducer).sendEmail(
                argThat(sentEmailData -> "Identity reverified for client: Client Name".equals(sentEmailData.getSubject())),
                eq("acsp_client_reverification_email"));
    }

    @Test
    void sendClientReverificationEmailFailure() throws EmailSendingException {
        doThrow(EmailSendingException.class).when(emailProducer).sendEmail(any(EmailData.class), eq("acsp_client_reverification_email"));

        String to = emailData.getTo();
        String clientName = emailData.getClientName();
        String referenceNumber = emailData.getReferenceNumber();
        String clientEmailAddress = emailData.getClientEmailAddress();

        assertThrows(EmailSendingException.class, () -> emailService.sendClientVerificationEmail(
                to,
                clientName,
                referenceNumber,
                clientEmailAddress,
                ApplicationType.REVERIFICATION));
    }
}
