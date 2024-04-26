package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.model.AcspData;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.util.Constants.*;

@Service
public class AcspService {
    private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);
    @Autowired
    AcspRepository acspRepository;

    public AcspData saveOrUpdateAcsp(AcspData acspData) {
        LOGGER.debug("received request in acsp service to save data");
        return acspRepository.save(acspData);
        //final String submissionId = insertedSubmission.getId();
        //final String submissionUri = getSubmissionUri(transaction.getId(), submissionId);
    }

    public AcspData getAcsp(String id) {
        LOGGER.debug("received request in acsp service to get data");
        Optional<AcspData> acspData = acspRepository.findById(id);
        return acspData.orElse(null);
    }


    private Resource createOverseasEntityTransactionResource(String submissionUri) {
        var overseasEntityResource = new Resource();
        overseasEntityResource.setKind(FILING_KIND_ACSP);

        Map<String, String> linksMap = new HashMap<>();
        linksMap.put("resource", submissionUri);
        linksMap.put("costs", submissionUri + COSTS_URI_SUFFIX);

        overseasEntityResource.setLinks(linksMap);
        return overseasEntityResource;
    }

    private String getSubmissionUri(String transactionId, String submissionId) {
        return String.format(SUBMISSION_URI_PATTERN, transactionId, submissionId);
    }
}
