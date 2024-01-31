package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.officers.OfficersResourceHandler;
import uk.gov.companieshouse.api.handler.officers.request.OfficersList;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.officers.CompanyOfficerApi;
import uk.gov.companieshouse.api.model.officers.OfficersApi;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfficerServiceTest {

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private ApiClient mockApiClient;

    @Mock
    private OfficersList mockOfficersList;

    @InjectMocks
    private OfficerService officerService;

    @Mock
    private OfficersResourceHandler mockResourceHandler;

    @Mock
    private ApiResponse<OfficersApi> mockApiResponse;

    private static final String COMPANY_NUMBER = "00000000";

    private static final String PASSTHROUGH_HEADER = "passthrough";
    private static final String OFFICERS_URI = "/company/00000000/officers";
    public static final String REGISTER_TYPE_DIRECTORS = "directors";


    @BeforeEach
    void init() throws IOException {
        when(apiClientService.getApiClient(PASSTHROUGH_HEADER)).thenReturn(mockApiClient);
        when(mockApiClient.officers()).thenReturn(mockResourceHandler);
        when(mockResourceHandler.list(OFFICERS_URI)).thenReturn(mockOfficersList);
    }
    @Test
    void testGetOfficersSuccessful() throws IOException, URIValidationException, ServiceException {
        when(mockOfficersList.execute()).thenReturn(mockApiResponse);
        OfficersApi officersApi = createOfficersApi();
        addOfficerItems(officersApi, 10);
        when(mockApiResponse.getData()).thenReturn(officersApi);

        OfficersApi returnedOfficers = officerService.getOfficers(PASSTHROUGH_HEADER, COMPANY_NUMBER, REGISTER_TYPE_DIRECTORS);

        assertNotNull(returnedOfficers);
        assertEquals(10, returnedOfficers.getItems().size());
    }

    @Test
    void testGetOfficersApiErrorResponseExceptionReturnsServiceException()
            throws ApiErrorResponseException, URIValidationException {
        when(mockOfficersList.execute()).thenThrow(ApiErrorResponseException.class);
        assertThrows(ServiceException.class, () -> officerService.getOfficers(PASSTHROUGH_HEADER, COMPANY_NUMBER, REGISTER_TYPE_DIRECTORS));
    }

    @Test
    void testGetOfficersURIValidationExceptionReturnsServiceException()
            throws ApiErrorResponseException, URIValidationException {
        when(mockOfficersList.execute()).thenThrow(URIValidationException.class);
        assertThrows(ServiceException.class, () -> officerService.getOfficers(PASSTHROUGH_HEADER, COMPANY_NUMBER, REGISTER_TYPE_DIRECTORS));
    }

    private OfficersApi createOfficersApi() {
        OfficersApi officersApi = new OfficersApi();
        officersApi.setActiveCount(20L);
        officersApi.setInactiveCount(20L);
        officersApi.setItemsPerPage(100L);
        officersApi.setKind("kind");
        officersApi.setEtag("etag");
        officersApi.setResignedCount(20);
        officersApi.setStartIndex(0);
        officersApi.setTotalResults(0);

        officersApi.setItems(new ArrayList<CompanyOfficerApi>());
        return officersApi;
    }

    private void addOfficerItems(OfficersApi officersApi, int numberOfOfficers) {
        for (int i = 0; i < numberOfOfficers; i++) {
            officersApi.getItems().add(new CompanyOfficerApi());
        }
    }
}
