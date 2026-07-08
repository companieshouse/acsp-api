package uk.gov.companieshouse.acsp.testutil;

import static uk.gov.companieshouse.acsp.util.Constants.ERIC_REQUEST_ID_KEY;
import static uk.gov.companieshouse.api.util.security.EricConstants.ERIC_AUTHORISED_KEY_ROLES;
import static uk.gov.companieshouse.api.util.security.EricConstants.ERIC_IDENTITY;
import static uk.gov.companieshouse.api.util.security.EricConstants.ERIC_IDENTITY_TYPE;
import org.springframework.http.HttpHeaders;

public class AuthTestUtil {

    public static final String X_REQUEST_ID = "your-request-id";

    public static final String PASSTHROUGH_HEADER = "passthrough";
    public static final String ERIC_ACCESS_TOKEN = "ERIC-Access-Token";

    public static final String KEY_ERIC_IDENTITY_TYPE = "key";
    public static final String KEY_ERIC_IDENTITY = "your-api-key";

    public static final String OAUTH2_ERIC_IDENTITY_TYPE = "oauth2";
    public static final String OAUTH2_ERIC_IDENTITY = "68669e5c62e7633d5768e8c4";

    private static final HttpHeaders apiKeyAuthorisationHeaders;
    private static final HttpHeaders oauth2AuthorisationHeaders;

    static {
        apiKeyAuthorisationHeaders = new HttpHeaders();
        apiKeyAuthorisationHeaders.add(ERIC_REQUEST_ID_KEY, X_REQUEST_ID);
        apiKeyAuthorisationHeaders.add(ERIC_IDENTITY, KEY_ERIC_IDENTITY);
        apiKeyAuthorisationHeaders.add(ERIC_IDENTITY_TYPE, KEY_ERIC_IDENTITY_TYPE);
        apiKeyAuthorisationHeaders.add(ERIC_AUTHORISED_KEY_ROLES, "*");

        oauth2AuthorisationHeaders = new HttpHeaders();
        oauth2AuthorisationHeaders.add(ERIC_REQUEST_ID_KEY, X_REQUEST_ID);
        oauth2AuthorisationHeaders.add(ERIC_IDENTITY, OAUTH2_ERIC_IDENTITY);
        oauth2AuthorisationHeaders.add(ERIC_IDENTITY_TYPE, OAUTH2_ERIC_IDENTITY_TYPE);
        // pass-through token header used by the controller/TransactionInterceptor
        oauth2AuthorisationHeaders.add(ERIC_ACCESS_TOKEN, PASSTHROUGH_HEADER);
    }

    public static HttpHeaders getApiKeyAuthorisationHeaders() {
        return AuthTestUtil.apiKeyAuthorisationHeaders;
    }

    public static HttpHeaders getOauth2AuthorisationHeaders() {
       return AuthTestUtil.oauth2AuthorisationHeaders;
    }
}
