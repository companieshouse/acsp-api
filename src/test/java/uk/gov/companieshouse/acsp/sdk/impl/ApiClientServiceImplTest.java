package uk.gov.companieshouse.acsp.sdk.impl;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiClientServiceImplTest {

    @Test
    void getApiClientReturnsSdkInstance() {
        try (MockedStatic<ApiSdkManager> sdkManager = mockStatic(ApiSdkManager.class)) {
            ApiClient mockClient = mock(ApiClient.class);
            sdkManager.when(ApiSdkManager::getSDK).thenReturn(mockClient);

            ApiClientServiceImpl service = new ApiClientServiceImpl();
            assertSame(mockClient, service.getApiClient());
        }
    }

    @Test
    void getApiClientWithHeaderReturnsSdkInstance() throws IOException {
        try (MockedStatic<ApiSdkManager> sdkManager = mockStatic(ApiSdkManager.class)) {
            ApiClient mockClient = mock(ApiClient.class);
            sdkManager.when(() -> ApiSdkManager.getSDK("header")).thenReturn(mockClient);

            ApiClientServiceImpl service = new ApiClientServiceImpl();
            assertSame(mockClient, service.getApiClient("header"));
        }
    }

    @Test
    void getInternalApiClientReturnsPrivateSdkInstance() {
        try (MockedStatic<ApiSdkManager> sdkManager = mockStatic(ApiSdkManager.class)) {
            InternalApiClient mockClient = mock(InternalApiClient.class);
            sdkManager.when(ApiSdkManager::getPrivateSDK).thenReturn(mockClient);

            ApiClientServiceImpl service = new ApiClientServiceImpl();
            assertSame(mockClient, service.getInternalApiClient());
        }
    }

    @Test
    void getInternalApiClientWithHeaderReturnsPrivateSdkInstance() throws IOException {
        try (MockedStatic<ApiSdkManager> sdkManager = mockStatic(ApiSdkManager.class)) {
            InternalApiClient mockClient = mock(InternalApiClient.class);
            sdkManager.when(() -> ApiSdkManager.getPrivateSDK("header")).thenReturn(mockClient);

            ApiClientServiceImpl service = new ApiClientServiceImpl();
            assertSame(mockClient, service.getInternalApiClient("header"));
        }
    }
}