package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.models.dao.CompanyDao;
import uk.gov.companieshouse.acsp.models.email.AcspEmailData;
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

    private final EmailProducer emailProducer;

    @Autowired
    public EmailService(EmailProducer emailProducer){
        this.emailProducer = emailProducer;
    }

    private void sendEmail(EmailData emailData, String messageType) throws EmailSendingException {
        try {
            emailProducer.sendEmail(emailData, messageType);
            LOGGER.info("Submitted email to Kafka" + messageType);
        } catch (EmailSendingException exception) {
            LOGGER.error("Error sending email", exception);
            throw exception;
        }
    }

    public void sendLimitedConfirmationEmail(String recipientEmail, CompanyDao companyDao, String applicationReference) {
        var emailData =  new AcspEmailData();
        emailData.setTo(recipientEmail);
        emailData.setSubject("Register as a Companies House authorised agent: application received");
        emailData.setCompanyNumber(companyDao.getCompanyNumber());
        emailData.setCompanyName(companyDao.getCompanyName());
        emailData.setApplicationReference(applicationReference);

        sendEmail(emailData, "acsp_application_received_limited_company");
    }

    public void sendConfirmationEmail(String recipientEmail, String businessName, String applicationReference) {
        var emailData =  new AcspEmailData();
        emailData.setTo(recipientEmail);
        emailData.setSubject("Register as a Companies House authorised agent: application received");
        emailData.setCompanyName(businessName);
        emailData.setApplicationReference(applicationReference);

        sendEmail(emailData, "acsp_application_received");
    }
}
