package uk.gov.companieshouse.acsp.exception;

public class ServiceException extends Exception {

    private int statusCode;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
