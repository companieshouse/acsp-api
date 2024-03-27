package uk.gov.companieshouse.acsp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.acsp.model.AcspData;
import uk.gov.companieshouse.acsp.service.AcspService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcspControllerTest {

    @InjectMocks
    private AcspController acspController;

    @Mock
    private AcspService acspService;

    @Test
    void saveAcsp(){
        AcspData acspData = new AcspData();
        when(acspService.saveOrUpdateAcsp(any())).thenReturn(acspData);
        var response = acspController.saveAcspData(acspData);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getAcsp(){
        AcspData acspData = new AcspData();
        when(acspService.getAcsp(any())).thenReturn(acspData);
        var response = acspController.getAcspData("demo@ch.gov.uk");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}