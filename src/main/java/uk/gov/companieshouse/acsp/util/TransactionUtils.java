package uk.gov.companieshouse.acsp.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.util.Map;
import java.util.Objects;

import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_ACSP;
import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_UPDATE_ACSP;
import static uk.gov.companieshouse.acsp.util.Constants.LINK_RESOURCE;

@Component
public class TransactionUtils {

    public boolean isTransactionLinkedToAcspSubmission(Transaction transaction, AcspDataDto acspDataDto) {
        Map<String, String> links;
        if(ObjectUtils.isNotEmpty(acspDataDto)){
            links = acspDataDto.getLinks();
            if (links == null || links.isEmpty()) {
                return false;
            }
        }else {
            return false;
        }
        String acspSubmissionSelfLink = links.get("self");
        if (StringUtils.isBlank(acspSubmissionSelfLink)) {
            return false;
        }

        if (Objects.isNull(transaction) || Objects.isNull(transaction.getResources())) {
            return false;
        }

        return transaction.getResources().entrySet().stream()
                .filter(resource -> FILING_KIND_ACSP.equals(resource.getValue().getKind()) || FILING_KIND_UPDATE_ACSP.equals((resource.getValue().getKind())))
                .anyMatch(resource -> acspSubmissionSelfLink.equals(resource.getValue().getLinks().get(LINK_RESOURCE)));
    }
}
