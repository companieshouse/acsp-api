package uk.gov.companieshouse.acsp.util;

public class Constants {

    private Constants() {}

    public static final String ERIC_REQUEST_ID_KEY = "X-Request-Id";
    public static final String TRANSACTION_ID_KEY = "transaction_id";
    public static final String TRANSACTION_KEY = "transaction";
    public static final String ACSP_APPLICATION_ID_KEY = "acsp_application_id";
    public static final String PAYMENT_REQUIRED_HEADER = "x-payment-required";
    public static final String FILING_KIND_ACSP = "acsp";
    public static final String FILING_KIND_UPDATE_ACSP = "acsp#update";
    public static final String FILING_KIND_CLOSE_ACSP = "acsp#cessation";
    public static final String COSTS_URI_SUFFIX = "/costs";
    public static final String SUBMISSION_URI_PATTERN = "/transactions/%s/authorised-corporate-service-provider-applications/%s";
    public static final String RESUME_JOURNEY_URI_PATTERN =
            "/register-as-companies-house-authorised-agent/resume?transactionId=%s&acspId=%s";
    public static final String LINK_SELF = "self";
    public static final String LINK_RESOURCE = "resource";
}
