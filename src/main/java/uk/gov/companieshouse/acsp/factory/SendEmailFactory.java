package uk.gov.companieshouse.acsp.factory;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.models.email.EmailData;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.UUID;

import static java.lang.String.format;
import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@Component
public class SendEmailFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

    private final String appId;

    private final JsonMapper jsonMapper;

    SendEmailFactory(@Value("${email.appId}") String appId, JsonMapper jsonMapper) {
        this.appId = appId;
        this.jsonMapper = jsonMapper;
    }

    public SendEmail createSendEmail(EmailData emailData, String messageType) throws JacksonException {
        var sendEmail = new SendEmail();
        sendEmail.setJsonData(this.jsonMapper.writeValueAsString(emailData));
        sendEmail.setEmailAddress(emailData.getTo());
        sendEmail.setAppId(appId);
        sendEmail.setMessageId(UUID.randomUUID().toString());
        sendEmail.setMessageType(messageType);
        LOGGER.info(format("Message created for email address %s", emailData.getTo()));
        return sendEmail;
    }

}