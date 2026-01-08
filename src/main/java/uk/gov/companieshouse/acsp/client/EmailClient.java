package uk.gov.companieshouse.acsp.client;


import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.companieshouse.acsp.exception.EmailSendException;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static java.lang.String.format;
import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@Component
public class EmailClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

    private final Supplier<InternalApiClient> internalApiClientSupplier;
    public EmailClient(
            Supplier<InternalApiClient> internalApiClientSupplier) {
        this.internalApiClientSupplier = internalApiClientSupplier;
    }

    public void sendEmail(final SendEmail sendEmail) throws EmailSendException {
        var requestId = getRequestId().orElse(UUID.randomUUID().toString());
        try {
            LOGGER.info(String.format("requestId is %s for email %s", requestId, sendEmail.getJsonData()));
            var apiClient = internalApiClientSupplier.get();
            apiClient.getHttpClient().setRequestId(requestId);

            var emailHandler = apiClient.sendEmailHandler();
            var emailPost = emailHandler.postSendEmail("/send-email", sendEmail);

            ApiResponse<Void> response = emailPost.execute();

            LOGGER.info(format("Posted '%s' email to CHS Kafka API: Response %d",
                    sendEmail.getJsonData(), response.getStatusCode()));

        } catch (ApiErrorResponseException ex) {
            LOGGER.error(format("Exception %s while sending email with requestId %s with email data %s", ex.getMessage(), requestId, sendEmail.getJsonData()));
            throw new EmailSendException(ex.getMessage());
        }
    }

    private Optional<String> getRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes.getRequest().getHeader("x-request-id"));
    }


}
