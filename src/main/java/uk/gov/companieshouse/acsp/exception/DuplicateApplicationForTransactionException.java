package uk.gov.companieshouse.acsp.exception;

public class DuplicateApplicationForTransactionException extends RuntimeException {
    public DuplicateApplicationForTransactionException(String message) {
        super(message);
    }
}
