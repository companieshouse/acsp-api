package uk.gov.companieshouse.acsp.exception;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceExceptionTest {

    final String MESSAGE_PARAM = "Test Message";
    final int STATUS_CODE = 404;
    final String INVALID_URI = "INVALIDURI";
    @Test
    void serviceExceptionObjectWithOneParam(){
        ServiceException serviceException = new ServiceException(MESSAGE_PARAM);

        assertEquals(serviceException.getMessage(), MESSAGE_PARAM);
    }

    @Test
    void serviceExceptionObjectWithTwoParams(){
        ServiceException serviceException = new ServiceException(STATUS_CODE, MESSAGE_PARAM);

        assertEquals(serviceException.getMessage(), MESSAGE_PARAM);
        assertEquals(serviceException.getStatusCode(), STATUS_CODE);
    }

    @Test
    void serviceExceptionObjectWithCause(){
        ServiceException serviceException = new ServiceException(MESSAGE_PARAM, new URIValidationException(INVALID_URI));

        assertEquals(serviceException.getMessage(), MESSAGE_PARAM);
    }
}
