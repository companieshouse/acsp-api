package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.model.AcspData;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;

@Service
public class AcspService {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    @Autowired
    AcspRepository acspRepository;

    public AcspData saveOrUpdateAcsp(AcspData acspData) {
        LOGGER.debug("received request in acsp service to save data");
        return acspRepository.save(acspData);
    }
}
