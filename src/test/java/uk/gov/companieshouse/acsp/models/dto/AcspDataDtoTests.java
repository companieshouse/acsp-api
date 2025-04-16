package uk.gov.companieshouse.acsp.models.dto;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.enums.AcspType;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;

class AcspDataDtoTests {

    @Test
    void correctlySetsAndGetsId() {
        AcspDataDto dto = new AcspDataDto();
        dto.setId("12345");
        assertEquals("12345", dto.getId());
    }

    @Test
    void correctlySetsAndGetsApplicantDetails() {
        AcspDataDto dto = new AcspDataDto();
        ApplicantDetailsDto applicantDetails = new ApplicantDetailsDto();
        dto.setApplicantDetails(applicantDetails);
        assertEquals(applicantDetails, dto.getApplicantDetails());
    }

    @Test
    void correctlySetsAndGetsRegisteredOfficeAddress() {
        AcspDataDto dto = new AcspDataDto();
        AddressDto address = new AddressDto();
        dto.setRegisteredOfficeAddress(address);
        assertEquals(address, dto.getRegisteredOfficeAddress());
    }

    @Test
    void correctlySetsAndGetsTypeOfBusiness() {
        AcspDataDto dto = new AcspDataDto();
        dto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        assertEquals(TypeOfBusiness.SOLE_TRADER, dto.getTypeOfBusiness());
    }

    @Test
    void correctlySetsAndGetsRoleType() {
        AcspDataDto dto = new AcspDataDto();
        dto.setRoleType(RoleType.DIRECTOR);
        assertEquals(RoleType.DIRECTOR, dto.getRoleType());
    }

    @Test
    void correctlySetsAndGetsVerified() {
        AcspDataDto dto = new AcspDataDto();
        dto.setVerified(true);
        assertTrue(dto.getVerified());
    }

    @Test
    void correctlySetsAndGetsBusinessName() {
        AcspDataDto dto = new AcspDataDto();
        dto.setBusinessName("Test Business");
        assertEquals("Test Business", dto.getBusinessName());
    }

    @Test
    void correctlySetsAndGetsWorkSector() {
        AcspDataDto dto = new AcspDataDto();
        dto.setWorkSector(BusinessSector.FI);
        assertEquals(BusinessSector.FI, dto.getWorkSector());
    }

    @Test
    void correctlySetsAndGetsAmlSupervisoryBodies() {
        AcspDataDto dto = new AcspDataDto();
        AMLSupervisoryBodiesDto[] bodies = new AMLSupervisoryBodiesDto[1];
        dto.setAmlSupervisoryBodies(bodies);
        assertArrayEquals(bodies, dto.getAmlSupervisoryBodies());
    }

    @Test
    void correctlySetsAndGetsRemovedAmlSupervisoryBodies() {
        AcspDataDto dto = new AcspDataDto();
        AMLSupervisoryBodiesDto[] removedBodies = new AMLSupervisoryBodiesDto[1];
        dto.setRemovedAmlSupervisoryBodies(removedBodies);
        assertArrayEquals(removedBodies, dto.getRemovedAmlSupervisoryBodies());
    }

    @Test
    void correctlySetsAndGetsCompanyDetails() {
        AcspDataDto dto = new AcspDataDto();
        CompanyDto company = new CompanyDto();
        dto.setCompanyDetails(company);
        assertEquals(company, dto.getCompanyDetails());
    }

    @Test
    void correctlySetsAndGetsCompanyAuthCodeProvided() {
        AcspDataDto dto = new AcspDataDto();
        dto.setCompanyAuthCodeProvided(true);
        assertTrue(dto.isCompanyAuthCodeProvided());
    }

    @Test
    void correctlySetsAndGetsAcspDataSubmission() {
        AcspDataDto dto = new AcspDataDto();
        AcspDataSubmissionDto submission = new AcspDataSubmissionDto();
        dto.setAcspDataSubmission(submission);
        assertEquals(submission, dto.getAcspDataSubmission());
    }

    @Test
    void correctlySetsAndGetsLinks() {
        AcspDataDto dto = new AcspDataDto();
        Map<String, String> links = Map.of("self", "/link");
        dto.setLinks(links);
        assertEquals(links, dto.getLinks());
    }

    @Test
    void correctlySetsAndGetsHowAreYouRegisteredWithAml() {
        AcspDataDto dto = new AcspDataDto();
        dto.setHowAreYouRegisteredWithAml("Directly");
        assertEquals("Directly", dto.getHowAreYouRegisteredWithAml());
    }

    @Test
    void correctlySetsAndGetsAcspType() {
        AcspDataDto dto = new AcspDataDto();
        dto.setAcspType(AcspType.REGISTER_ACSP);
        assertEquals(AcspType.REGISTER_ACSP, dto.getAcspType());
    }

    @Test
    void correctlySetsAndGetsAcspId() {
        AcspDataDto dto = new AcspDataDto();
        dto.setAcspId("ACSP123");
        assertEquals("ACSP123", dto.getAcspId());
    }

    @Test
    void returnsNullForUnsetFields() {
        AcspDataDto dto = new AcspDataDto();
        assertNull(dto.getId());
        assertNull(dto.getApplicantDetails());
        assertNull(dto.getRegisteredOfficeAddress());
        assertNull(dto.getTypeOfBusiness());
        assertNull(dto.getRoleType());
        assertNull(dto.getVerified());
        assertNull(dto.getBusinessName());
        assertNull(dto.getWorkSector());
        assertNull(dto.getAmlSupervisoryBodies());
        assertNull(dto.getRemovedAmlSupervisoryBodies());
        assertNull(dto.getCompanyDetails());
        assertNull(dto.getAcspDataSubmission());
        assertNull(dto.getLinks());
        assertNull(dto.getHowAreYouRegisteredWithAml());
        assertNull(dto.getAcspType());
        assertNull(dto.getAcspId());
    }
}