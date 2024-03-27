package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.model.AcspData;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;

import java.util.Optional;

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
        AcspData response = acspService.saveOrUpdateAcsp(acspData);
        assertEquals(acspData.getId(), response.getId());
    }

    @Test
    void getAcsp(){
        AcspData acspData = new AcspData();
        acspData.setId("demo@ch.gov.uk");
        when(acspRepository.findById(any())).thenReturn(Optional.of(acspData));
        AcspData response = acspService.getAcsp("demo@ch.gov.uk");
        assertEquals(acspData.getId(), response.getId());
    }
}