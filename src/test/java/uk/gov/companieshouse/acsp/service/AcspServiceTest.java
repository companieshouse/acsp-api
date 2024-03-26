package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.model.AcspData;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcspServiceTest {
    @InjectMocks
    private AcspService acspService;

    @Mock
    private AcspRepository acspRepository;

    @Test
    void saveAcsp(){
        AcspData acspData = new AcspData();
        acspData.setId("demo@ch.gov.uk");
        when(acspRepository.save(any())).thenReturn(acspData);
        var response = acspService.saveOrUpdateAcsp(acspData);
        assertEquals(acspData.getId(), response.getId());
    }
}