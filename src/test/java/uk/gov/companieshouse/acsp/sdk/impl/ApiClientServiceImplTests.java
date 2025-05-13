package uk.gov.companieshouse.acsp.sdk.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiClientServiceImplTests {

    private final ApiSdkManager apiSdkManagerMock = mock(ApiSdkManager.class);
    private final ApiClientServiceImpl apiClientService = new ApiClientServiceImpl();

    @Test
    void returnsApiClientSuccessfully() {
        try (MockedStatic<ApiSdkManager> mockedStatic = mockStatic(ApiSdkManager.class)) {
            ApiClient apiClient = mock(ApiClient.class);
            mockedStatic.when(ApiSdkManager::getSDK).thenReturn(apiClient);

            ApiClient result = apiClientService.getApiClient();

            assertNotNull(result);
            mockedStatic.verify(ApiSdkManager::getSDK, times(1));
        }
    }

    @Test
    void returnsApiClientWithPassthroughHeaderSuccessfully() throws IOException {
        try (MockedStatic<ApiSdkManager> mockedStatic = mockStatic(ApiSdkManager.class)) {
            ApiClient apiClient = mock(ApiClient.class);
            String passthroughHeader = "header-value";
            mockedStatic.when(() -> ApiSdkManager.getSDK(passthroughHeader)).thenReturn(apiClient);

            ApiClient result = apiClientService.getApiClient(passthroughHeader);

            assertNotNull(result);
            mockedStatic.verify(() -> ApiSdkManager.getSDK(passthroughHeader), times(1));
        }
    }

    @Test
    void returnsInternalApiClientSuccessfully() {
        try (MockedStatic<ApiSdkManager> mockedStatic = mockStatic(ApiSdkManager.class)) {
            InternalApiClient internalApiClient = mock(InternalApiClient.class);
            mockedStatic.when(ApiSdkManager::getPrivateSDK).thenReturn(internalApiClient);

            InternalApiClient result = apiClientService.getInternalApiClient();

            assertNotNull(result);
            mockedStatic.verify(ApiSdkManager::getPrivateSDK, times(1));
        }
    }

    @Test
    void returnsInternalApiClientWithPassthroughHeaderSuccessfully() throws IOException {
        try (MockedStatic<ApiSdkManager> mockedStatic = mockStatic(ApiSdkManager.class)) {
            InternalApiClient internalApiClient = mock(InternalApiClient.class);
            String passthroughHeader = "header-value";
            mockedStatic.when(() -> ApiSdkManager.getPrivateSDK(passthroughHeader)).thenReturn(internalApiClient);

            InternalApiClient result = apiClientService.getInternalApiClient(passthroughHeader);

            assertNotNull(result);
            mockedStatic.verify(() -> ApiSdkManager.getPrivateSDK(passthroughHeader), times(1));
        }
    }
}