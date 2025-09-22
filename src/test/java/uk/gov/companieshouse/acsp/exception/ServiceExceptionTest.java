package uk.gov.companieshouse.acsp.exception;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceExceptionTest {

    final String messageParam = "Test Message";
    final int statusCode = 404;
    final String invalidUri = "INVALIDURI";

    @Test
    void serviceExceptionObjectWithOneParam(){
        ServiceException serviceException = new ServiceException(messageParam);

        assertEquals(serviceException.getMessage(), messageParam);
    }

    @Test
    void serviceExceptionObjectWithTwoParams(){
        ServiceException serviceException = new ServiceException(statusCode, messageParam);

        assertEquals(serviceException.getMessage(), messageParam);
        assertEquals(serviceException.getStatusCode(), statusCode);
    }

    @Test
    void serviceExceptionObjectWithCause(){
        ServiceException serviceException = new ServiceException(messageParam, new URIValidationException(invalidUri));

        assertEquals(serviceException.getMessage(), messageParam);
    }
}
