package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriTemplate;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.officers.request.OfficersList;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@Service
public class OfficerService {

    private final ApiClientService apiClientService;

    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

    @Autowired
    public OfficerService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    private static final UriTemplate GET_OFFICERS_URI =
        new UriTemplate("/company/{companyNumber}/officers");

    public OfficersApi getOfficers(String passThroughHeader, String companyNumber) throws ServiceException {
        try {
            ApiClient apiClient = apiClientService.getApiClient(passThroughHeader);

            OfficersApi officersApi;
            int startIndex = 0;
            int itemsPerPage = 100;

            officersApi = retrieveOfficerAppointments(companyNumber, apiClient, startIndex, itemsPerPage);

            while (officersApi.getItems().size() < officersApi.getTotalResults()) {
                try {
                    startIndex += itemsPerPage;
                    OfficersApi moreResults = retrieveOfficerAppointments(companyNumber, apiClient, startIndex, itemsPerPage);
                    officersApi.getItems().addAll(moreResults.getItems());
                } catch (ServiceException se) {
                    if (!CollectionUtils.isEmpty(officersApi.getItems())) {
                        LOGGER.error("Possible data discrepancy while retrieving directors for " + companyNumber);
                        return officersApi;
                    } else {
                        throw se;
                    }
                }
            }
            return officersApi;
        } catch (IOException e) {
            throw new ServiceException("Error Retrieving directors for " + companyNumber, e);
        }
    }

    private OfficersApi retrieveOfficerAppointments(String companyNumber, ApiClient apiClient, Integer startIndex, Integer itemsPerPage)
            throws ServiceException {
        String uri = GET_OFFICERS_URI.expand(companyNumber).toString();
        OfficersApi officersApi = null;
        try {
            OfficersList officersList = apiClient.officers().list(uri);
            officersList.addQueryParams("items_per_page", itemsPerPage.toString());
            officersList.addQueryParams("start_index", startIndex.toString());
            officersList.addQueryParams("register_type", "directors");
            officersList.addQueryParams("register_view", "true");

            officersApi = officersList.execute().getData();
        } catch (ApiErrorResponseException e) {
            throw new ServiceException("Error retrieving officers", e);
        } catch (URIValidationException e) {
            throw new ServiceException("Invalid URI for officers resource", e);
        }
        return officersApi;
    }
}
