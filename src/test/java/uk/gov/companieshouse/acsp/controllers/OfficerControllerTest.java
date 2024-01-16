package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.OfficerService;
import uk.gov.companieshouse.api.model.officers.OfficersApi;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfficerControllerTest {

    @InjectMocks
    private OfficerController officerController;
    @Mock
    private HttpServletRequest request;

    @Mock
    private OfficerService officerService;
    private static final String COMPANY_ID = "GFEDCBA";
    private static final String PASSTHROUGH_HEADER = "passthrough";
    public static final String REGISTER_TYPE_DIRECTORS = "directors";

    @Test
    void getOfficers() throws  ServiceException {
        OfficersApi dummyOfficersApi = new OfficersApi();
        dummyOfficersApi.setEtag("123");
        when(officerService.getOfficers(PASSTHROUGH_HEADER, COMPANY_ID, REGISTER_TYPE_DIRECTORS)).thenReturn(dummyOfficersApi);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = officerController.getOfficers(COMPANY_ID, REGISTER_TYPE_DIRECTORS, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTransactionServiceException() throws ServiceException {
        doThrow(new ServiceException("ERROR", new IOException())).when(officerService)
                .getOfficers(PASSTHROUGH_HEADER, COMPANY_ID, REGISTER_TYPE_DIRECTORS);
        when(request.getHeader(ApiSdkManager.getEricPassthroughTokenHeader())).thenReturn(PASSTHROUGH_HEADER);
        var response = officerController.getOfficers(COMPANY_ID, REGISTER_TYPE_DIRECTORS, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}
