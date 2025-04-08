package uk.gov.companieshouse.acsp.exception;

public class ServiceException extends Exception {

    private final int statusCode;

    public ServiceException(String message) {
        super(message);
        this.statusCode = 0; // Default value or appropriate initialization
    }

    public ServiceException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0; // Default value or appropriate initialization
    }

    public int getStatusCode() {
        return statusCode;
    }
}