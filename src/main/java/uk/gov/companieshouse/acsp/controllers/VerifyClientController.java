package uk.gov.companieshouse.acsp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.acsp.service.EmailService;

@RestController
@RequestMapping("/acsp-api")
public class VerifyClientController {

    private final EmailService emailService;

    public VerifyClientController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-identity-verification-email")
    public ResponseEntity<?> sendIdentityVerificationEmail(@RequestBody ClientVerificationEmailData clientVerificationEmailDao) {
        try{
            emailService.sendClientVerificationEmail(
                    clientVerificationEmailDao.getTo(),
                    clientVerificationEmailDao.getClientName(),
                    clientVerificationEmailDao.getReferenceNumber(),
                    clientVerificationEmailDao.getClientEmailAddress());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
