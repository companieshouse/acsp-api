package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.repositories.ACSPRepository;

@Service
public class ACSPService {
    @Autowired
    ACSPRepository acspRepository;
}
