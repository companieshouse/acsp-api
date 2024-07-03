package uk.gov.companieshouse.acsp.util;

public class Constants {

    private Constants() {}


    public static final String ERIC_IDENTITY = "ERIC-identity";
    public static final String ERIC_ACCESS_TOKEN = "ERIC-Access-Token";
    public static final String ERIC_REQUEST_ID_KEY = "X-Request-Id";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String TRANSACTION_KEY = "transaction";
    public static final String ACSP_APPLICATION_ID_KEY = "acsp_id";
    public static final String TRANSACTIONS_PRIVATE_API_PREFIX = "/private/transactions/";
    public static final String FILING_KIND_CS = "acsp";
    public static final String PAYMENT_REQUIRED_HEADER = "x-payment-required";
    public static final String TRUE = "true";
    public static final String FILING_KIND_ACSP = "acsp";
    public static final String COSTS_URI_SUFFIX = "/costs";
    public static final String SUBMISSION_URI_PATTERN = "/transactions/%s/acsp/%s";
    public static final String RESUME_JOURNEY_URI_PATTERN =
            "/register-as-companies-house-authorised-agent/resume?transactionId=%s&acspId=%s";
    public static final String LINK_SELF = "self";
    public static final String LINK_VALIDATION = "validation_status";
    public static final String LINK_RESOURCE = "resource";
}
