package uk.gov.companieshouse.acsp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.client.EmailClient;
import uk.gov.companieshouse.acsp.exception.EmailSendException;
import uk.gov.companieshouse.acsp.factory.SendEmailFactory;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.acsp.models.email.EmailData;
import uk.gov.companieshouse.acsp.models.enums.ApplicationType;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static java.lang.String.format;
import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    private static final String VERIFY_EMAIL_SUBJECT = "Identity verified for client: ";
    private static final String REVERIFY_EMAIL_SUBJECT = "Identity reverified for client: ";
    public static final String ACSP_CLIENT_REVERIFICATION_EMAIL = "acsp_client_reverification_email";
    public static final String ACSP_CLIENT_VERIFICATION_EMAIL = "acsp_client_verification_email";
    private final EmailClient emailClient;
    private final SendEmailFactory sendEmailFactory;

    public EmailService(EmailClient emailClient, SendEmailFactory sendEmailFactory) {
        this.emailClient = emailClient;
        this.sendEmailFactory = sendEmailFactory;
    }

    public void sendClientVerificationEmail(final String recipientEmailAddress, final String clientName, final String referenceNumber, final String clientEmailAddress, final ApplicationType applicationType) {
        LOGGER.info(format("Sending client %s email for application: %s", applicationType.getLabel(), referenceNumber));

        final var emailData = new ClientVerificationEmailData();
        emailData.setClientName(clientName);
        emailData.setReferenceNumber(referenceNumber);
        emailData.setClientEmailAddress(clientEmailAddress);
        emailData.setTo(recipientEmailAddress);

        String messageType = ApplicationType.REVERIFICATION.equals(applicationType)
                ? ACSP_CLIENT_REVERIFICATION_EMAIL
                : ACSP_CLIENT_VERIFICATION_EMAIL;

        String subjectPrefix = ApplicationType.REVERIFICATION.equals(applicationType)
                ? REVERIFY_EMAIL_SUBJECT
                : VERIFY_EMAIL_SUBJECT;

        emailData.setSubject(subjectPrefix + clientName);

        try {
            sendEmail(emailData, messageType);
        } catch (JsonProcessingException e) {
            LOGGER.error(format("Failed to process JSON for referenceNumber: %s", referenceNumber));
            throw new EmailSendException("Error encoding email data: " + e.getMessage());
        }
    }

    private void sendEmail(final EmailData emailData, final String messageType) throws EmailSendException, JsonProcessingException {
        var sendEmail = sendEmailFactory.createSendEmail(emailData, messageType);
        emailClient.sendEmail(sendEmail);
    }
}
