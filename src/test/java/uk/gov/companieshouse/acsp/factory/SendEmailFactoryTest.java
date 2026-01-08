package uk.gov.companieshouse.acsp.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.acsp.models.email.ClientVerificationEmailData;
import uk.gov.companieshouse.api.chskafka.SendEmail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
class SendEmailFactoryTest {

    @Value("${email.appId}")
    private String appId;
    private static final String MESSAGE_TYPE = "verification";
    @Mock
    private ObjectMapper objectMapper;

    private SendEmailFactory sendEmailFactory;

    @BeforeEach
    void setUp() {
        sendEmailFactory = new SendEmailFactory(appId, objectMapper);
    }

    @Test
    void testCreateSendEmail() throws JsonProcessingException {
        // Arrange
        var emailData = new ClientVerificationEmailData();
        emailData.setClientName("Client Name");
        emailData.setReferenceNumber("12345");
        emailData.setClientEmailAddress("client@example.com");
        emailData.setTo("recipient@example.com");
        String jsonData = "{\"to\":\"recipient@example.com\"}";
        when(objectMapper.writeValueAsString(emailData)).thenReturn(jsonData);

        // Act
        SendEmail sendEmail = sendEmailFactory.createSendEmail(emailData, MESSAGE_TYPE);

        // Assert
        assertEquals(jsonData, sendEmail.getJsonData());
        assertEquals("recipient@example.com", sendEmail.getEmailAddress());
        assertEquals(appId, sendEmail.getAppId());
        assertEquals(MESSAGE_TYPE, sendEmail.getMessageType());
    }
}
