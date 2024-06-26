package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.models.dao.CompanyDao;
import uk.gov.companieshouse.acsp.models.email.AcspEmailData;
import uk.gov.companieshouse.email_producer.EmailProducer;
import uk.gov.companieshouse.email_producer.EmailSendingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String APPLICATION_REFERENCE = "43462387648";
    private static final String EMAIL_ADDRESS = "test@email.com";

    @InjectMocks
    private EmailService emailService;

    @Mock
    private EmailProducer emailProducer;

    @Test
    void sendEmailThrowsException(){
        doThrow(new EmailSendingException("Test Exception", new Throwable())).when(emailProducer).sendEmail(any(AcspEmailData.class), eq("acsp_application_received"));
        try{
            emailService.sendConfirmationEmail(EMAIL_ADDRESS, "Test Business", APPLICATION_REFERENCE);
        } catch (EmailSendingException e) {
            verify(emailProducer, times(1)).sendEmail(any(AcspEmailData.class), eq("acsp_application_received"));
            assertEquals("Test Exception", e.getMessage());
        }
    }

    @Test
    void sendLimitedConfirmationEmailSuccess(){
        CompanyDao companyDao = new CompanyDao();
        companyDao.setCompanyName("Test Company 123");
        companyDao.setCompanyNumber("12345678");
        emailService.sendLimitedConfirmationEmail(EMAIL_ADDRESS, companyDao, APPLICATION_REFERENCE);

        verify(emailProducer, times(1)).sendEmail(any(AcspEmailData.class), eq("acsp_application_received_limited_company"));
    }

    @Test
    void sendConfirmationEmailSuccess(){
        emailService.sendConfirmationEmail(EMAIL_ADDRESS, "Test Business", APPLICATION_REFERENCE);

        verify(emailProducer, times(1)).sendEmail(any(AcspEmailData.class), eq("acsp_application_received"));
    }
}
