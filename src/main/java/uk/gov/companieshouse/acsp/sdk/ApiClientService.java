package uk.gov.companieshouse.acsp.sdk;

import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;

import java.io.IOException;

/**
 * The {@code ApiClientService} interface provides an abstraction that can be
 * used when testing {@code ApiSdkManager} static methods, without imposing
 * the use of a test framework that supports mocking of static methods.
 */
public interface ApiClientService {
    ApiClient getApiClient();
    ApiClient getApiClient(String passThroughHeader) throws IOException;

    InternalApiClient getInternalApiClient();
    InternalApiClient getInternalApiClient(String passThroughHeader) throws IOException;
}
