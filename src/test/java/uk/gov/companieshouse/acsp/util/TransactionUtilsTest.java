package uk.gov.companieshouse.acsp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.dto.AcspDataSubmissionDto;
import uk.gov.companieshouse.acsp.models.dto.ApplicantDetailsDto;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_ACSP;
import static uk.gov.companieshouse.acsp.util.Constants.LINK_RESOURCE;

@ExtendWith(MockitoExtension.class)
class TransactionUtilsTest {

    private static final String ACSP_SELF_LINK = "/transaction/1234/acsp/1234";
    private static final String ACSP_ID = "abc123";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    @Mock
    private Transaction transaction;


    private AcspDataDto acspDataDto;
    private final TransactionUtils transactionUtils = new TransactionUtils();

    @Test
    void testIsTransactionLinkedToAcspSubmissionReturnsFalseWhenAcspSelfLinkIsNull() {
        var result = transactionUtils.isTransactionLinkedToAcspSubmission(transaction, null);
        assertFalse(result);
    }

    @Test
    void testIsTransactionLinkedToAcspSubmissionReturnsFalseWhenTransactionIsNull() {
        var result = transactionUtils.isTransactionLinkedToAcspSubmission(null, getAcspDataDto());
        assertFalse(result);
    }

    @Test
    void testIsTransactionLinkedToAcspSubmissionReturnsFalseIfTransactionResourcesIsNull() {
        when(transaction.getResources()).thenReturn(null);

        var result = transactionUtils.isTransactionLinkedToAcspSubmission(transaction, getAcspDataDto());
        assertFalse(result);
    }

    @Test
    void testIsTransactionLinkedToAcspSubmissionReturnsFalseIfNoAcspFilingKindFoundInTransaction() {
        Map<String, Resource> transactionResources = new HashMap<>();
        Resource accountsResource = new Resource();
        accountsResource.setKind("Accounts");
        when(transaction.getResources()).thenReturn(transactionResources);

        var result = transactionUtils.isTransactionLinkedToAcspSubmission(transaction, getAcspDataDto());
        assertFalse(result);
    }

    @Test
    void testIsTransactionLinkedToAcspSubmissionReturnsFalseIfNoAcspMatchFound() {
        Map<String, Resource> transactionResources = new HashMap<>();
        Resource acspResource = new Resource();
        acspResource.setKind(FILING_KIND_ACSP);
        Map<String, String> acspResourceLinks = new HashMap<>();
        String nonMatchingResourceLink = "/transaction/1234/acsp/should-not-match";
        acspResourceLinks.put(LINK_RESOURCE, nonMatchingResourceLink);
        acspResource.setLinks(acspResourceLinks);
        transactionResources.put(nonMatchingResourceLink, acspResource);
        when(transaction.getResources()).thenReturn(transactionResources);

        var result = transactionUtils.isTransactionLinkedToAcspSubmission(transaction, getAcspDataDto());
        assertFalse(result);
    }

    @Test
    void testIsTransactionLinkedToAcspSubmissionReturnsTrueIfAcspMatchFound() {
        Map<String, Resource> transactionResources = new HashMap<>();
        Resource acspResource = new Resource();
        acspResource.setKind(FILING_KIND_ACSP);
        Map<String, String> acspResourceLinks = new HashMap<>();
        acspResourceLinks.put(LINK_RESOURCE, ACSP_SELF_LINK);
        acspResource.setLinks(acspResourceLinks);
        transactionResources.put(ACSP_SELF_LINK, acspResource);
        when(transaction.getResources()).thenReturn(transactionResources);

        var result = transactionUtils.isTransactionLinkedToAcspSubmission(transaction, getAcspDataDto());
        assertTrue(result);
    }


    private AcspDataDto getAcspDataDto() {
        acspDataDto = new AcspDataDto();
        acspDataDto.setId(ACSP_ID);

        ApplicantDetailsDto applicantDetails = new ApplicantDetailsDto();
        applicantDetails.setFirstName(FIRST_NAME);
        applicantDetails.setLastName(LAST_NAME);

        acspDataDto.setApplicantDetails(applicantDetails);

        acspDataDto.setEmail("email@email.com");
        AcspDataSubmissionDto dataSubmissionDto = new AcspDataSubmissionDto();
        Map<String, String> linksMap = Map.of("self", ACSP_SELF_LINK);
        acspDataDto.setLinks(linksMap);
        acspDataDto.setAcspDataSubmission(dataSubmissionDto);
        return acspDataDto;
    }

}
