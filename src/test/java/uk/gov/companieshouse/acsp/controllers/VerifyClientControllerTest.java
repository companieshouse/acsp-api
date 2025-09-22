package uk.gov.companieshouse.acsp.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.acsp.models.enums.ApplicationType;
import uk.gov.companieshouse.acsp.service.EmailService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class VerifyClientControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerifyClientController verifyClientController;

    private ClientVerificationEmailData emailData;

    @BeforeEach
    void setUp() {
        emailData = new ClientVerificationEmailData();
        emailData.setTo("recipient@example.com");
        emailData.setClientName("Client Name");
        emailData.setReferenceNumber("12345");
        emailData.setClientEmailAddress("client@example.com");
    }

    @Test
    void sendIdentityVerificationEmailSuccess() {
        ResponseEntity<?> response = verifyClientController.sendIdentityVerificationEmail(emailData, "verification");

        verify(emailService).sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress(),
                ApplicationType.VERIFICATION);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void sendIdentityVerificationEmailSuccessWithDefaultApplicationType() {
        ResponseEntity<?> response = verifyClientController.sendIdentityVerificationEmail(emailData, null);

        verify(emailService).sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress(),
                ApplicationType.VERIFICATION);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void sendIdentityReverificationEmailSuccess() {
        ResponseEntity<?> response = verifyClientController.sendIdentityVerificationEmail(emailData, "reverification");

        verify(emailService).sendClientVerificationEmail(
                emailData.getTo(),
                emailData.getClientName(),
                emailData.getReferenceNumber(),
                emailData.getClientEmailAddress(),
                ApplicationType.REVERIFICATION);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void sendIdentityVerificationEmailFailure() {
        doThrow(RuntimeException.class).when(emailService)
                .sendClientVerificationEmail(
                        emailData.getTo(),
                        emailData.getClientName(),
                        emailData.getReferenceNumber(),
                        emailData.getClientEmailAddress(),
                        ApplicationType.VERIFICATION);

        ResponseEntity<?> response = verifyClientController.sendIdentityVerificationEmail(emailData, "verification");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void sendIdentityReverificationEmailFailure() {
        doThrow(RuntimeException.class).when(emailService)
                .sendClientVerificationEmail(
                        emailData.getTo(),
                        emailData.getClientName(),
                        emailData.getReferenceNumber(),
                        emailData.getClientEmailAddress(),
                        ApplicationType.REVERIFICATION);

        ResponseEntity<?> response = verifyClientController.sendIdentityVerificationEmail(emailData, "reverification");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
