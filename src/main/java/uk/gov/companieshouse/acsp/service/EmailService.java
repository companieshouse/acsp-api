package uk.gov.companieshouse.acsp.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.email_producer.EmailProducer;
import uk.gov.companieshouse.email_producer.EmailSendingException;
import uk.gov.companieshouse.email_producer.model.EmailData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@Service
@ComponentScan("uk.gov.companieshouse.email_producer")
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    private static final String EMAIL_SUBJECT = "Identity verified for client: ";
    private final EmailProducer emailProducer;

    public EmailService(EmailProducer emailProducer) {
        this.emailProducer = emailProducer;
    }

    public void sendClientVerificationEmail(final String recipientEmailAddress, final String clientName, final String referenceNumber, final String clientEmailAddress) {
        LOGGER.info("Sending client verification email for application: " + referenceNumber);

        final var emailData = new ClientVerificationEmailData();
        emailData.setClientName(clientName);
        emailData.setReferenceNumber(referenceNumber);
        emailData.setClientEmailAddress(clientEmailAddress);
        emailData.setTo(recipientEmailAddress);
        emailData.setSubject(EMAIL_SUBJECT + clientName);
        sendEmail(emailData, "acsp_client_verification_email");
    }

    private void sendEmail(final EmailData emailData, final String messageType) throws EmailSendingException {
        emailProducer.sendEmail(emailData, messageType);
    }
}
