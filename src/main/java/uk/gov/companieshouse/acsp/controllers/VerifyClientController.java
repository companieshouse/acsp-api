package uk.gov.companieshouse.acsp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.acsp.service.EmailService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@RestController
@RequestMapping("/acsp-api/verify-client-identity")
public class VerifyClientController {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    private final EmailService emailService;

    public VerifyClientController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-identity-verification-email")
    public ResponseEntity<String> sendIdentityVerificationEmail(@RequestBody ClientVerificationEmailData clientVerificationEmailDao) {
        try{
            emailService.sendClientVerificationEmail(
                    clientVerificationEmailDao.getTo(),
                    clientVerificationEmailDao.getClientName(),
                    clientVerificationEmailDao.getReferenceNumber(),
                    clientVerificationEmailDao.getClientEmailAddress());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            LOGGER.error("Error sending email for identity-verification " + e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
