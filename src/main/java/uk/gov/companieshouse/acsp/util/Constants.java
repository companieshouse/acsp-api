package uk.gov.companieshouse.acsp.util;

public class Constants {

    private Constants() {}

    public static final String ERIC_REQUEST_ID_KEY = "X-Request-Id";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String FILING_KIND_CS = "acsp";
    public static final String PAYMENT_REQUIRED_HEADER = "x-payment-required";
    public static final String TRUE = "true";
    public static final String FILING_KIND_ACSP = "acsp";
    public static final String COSTS_URI_SUFFIX = "/costs";
    public static final String SUBMISSION_URI_PATTERN = "/transactions/%s/overseas-entity/%s";
}
