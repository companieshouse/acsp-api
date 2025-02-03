package uk.gov.companieshouse.acsp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.acsp.service.EmailService;
import uk.gov.companieshouse.email_producer.EmailSendingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class VerifyClientControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerifyClientController verifyClientController;

    @Test
    void sendIdentityVerificationEmailSuccess() {
        var clientVerificationEmailData = new ClientVerificationEmailData();
        clientVerificationEmailData.setTo("recipient@example.com");
        clientVerificationEmailData.setClientName("Client Name");
        clientVerificationEmailData.setReferenceNumber("12345");
        clientVerificationEmailData.setClientEmailAddress("client@example.com");

        ResponseEntity<?> response = verifyClientController.sendIdentityVerificationEmail(clientVerificationEmailData);

        verify(emailService).sendClientVerificationEmail(
                "recipient@example.com",
                "Client Name",
                "12345",
                "client@example.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void sendIdentityVerificationEmailFailure() {
        var clientVerificationEmailData = new ClientVerificationEmailData();
        clientVerificationEmailData.setTo("recipient@example.com");
        clientVerificationEmailData.setClientName("Client Name");
        clientVerificationEmailData.setReferenceNumber("12345");
        clientVerificationEmailData.setClientEmailAddress("client@example.com");

        doThrow(EmailSendingException.class).when(emailService).sendClientVerificationEmail(
                "recipient@example.com",
                "Client Name",
                "12345",
                "client@example.com");

        ResponseEntity<?> response = verifyClientController.sendIdentityVerificationEmail(clientVerificationEmailData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }


}
