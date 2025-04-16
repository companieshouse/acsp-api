package uk.gov.companieshouse.acsp.models.dao;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AcspDataDaoTests {

    @Test
    void correctlySetsAndGetsId() {
        AcspDataDao acspData = new AcspDataDao();
        acspData.setId("12345");
        assertEquals("12345", acspData.getId());
    }

    @Test
    void correctlySetsAndGetsApplicantDetails() {
        ApplicantDetailsDao applicantDetails = new ApplicantDetailsDao();
        applicantDetails.setFirstName("John");
        AcspDataDao acspData = new AcspDataDao();
        acspData.setApplicantDetails(applicantDetails);
        assertEquals(applicantDetails, acspData.getApplicantDetails());
    }

    @Test
    void correctlySetsAndGetsRegisteredOfficeAddress() {
        AddressDao address = new AddressDao();
        address.setAddressLine1("456 Elm St");
        AcspDataDao acspData = new AcspDataDao();
        acspData.setRegisteredOfficeAddress(address);
        assertEquals(address, acspData.getRegisteredOfficeAddress());
    }

    @Test
    void correctlySetsAndGetsVerified() {
        AcspDataDao acspData = new AcspDataDao();
        acspData.setVerified(true);
        assertTrue(acspData.getVerified());
    }

    @Test
    void correctlySetsAndGetsAmlSupervisoryBodies() {
        AMLSupervisoryBodiesDao[] amlBodies = new AMLSupervisoryBodiesDao[1];
        amlBodies[0] = new AMLSupervisoryBodiesDao();
        AcspDataDao acspData = new AcspDataDao();
        acspData.setAmlSupervisoryBodies(amlBodies);
        assertArrayEquals(amlBodies, acspData.getAmlSupervisoryBodies());
    }

    @Test
    void correctlySetsAndGetsRemovedAmlSupervisoryBodies() {
        AMLSupervisoryBodiesDao[] removedAmlBodies = new AMLSupervisoryBodiesDao[1];
        removedAmlBodies[0] = new AMLSupervisoryBodiesDao();
        AcspDataDao acspData = new AcspDataDao();
        acspData.setRemovedAmlSupervisoryBodies(removedAmlBodies);
        assertArrayEquals(removedAmlBodies, acspData.getRemovedAmlSupervisoryBodies());
    }

    @Test
    void correctlySetsAndGetsLinks() {
        Map<String, String> links = Map.of("self", "/acsp/12345");
        AcspDataDao acspData = new AcspDataDao();
        acspData.setLinks(links);
        assertEquals(links, acspData.getLinks());
    }

    @Test
    void returnsNullForUnsetFields() {
        AcspDataDao acspData = new AcspDataDao();
        assertNull(acspData.getId());
        assertNull(acspData.getApplicantDetails());
        assertNull(acspData.getRegisteredOfficeAddress());
        assertNull(acspData.getTypeOfBusiness());
        assertNull(acspData.getRoleType());
        assertNull(acspData.getVerified());
        assertNull(acspData.getBusinessName());
        assertNull(acspData.getWorkSector());
        assertNull(acspData.getAmlSupervisoryBodies());
        assertNull(acspData.getRemovedAmlSupervisoryBodies());
        assertNull(acspData.getCompanyDetails());
        assertNull(acspData.getLinks());
        assertNull(acspData.getHowAreYouRegisteredWithAml());
        assertNull(acspData.getAcspType());
        assertNull(acspData.getAcspId());
    }

    @Test
    void returnsFalseForUnsetBooleanField() {
        AcspDataDao acspData = new AcspDataDao();
        assertFalse(acspData.isCompanyAuthCodeProvided());
    }
}