package uk.gov.companieshouse.acsp.models.dao;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ApplicantDetailsDaoTests {

    @Test
    void correctlySetsAndGetsFirstName() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setFirstName("John");
        assertEquals("John", applicant.getFirstName());
    }

    @Test
    void correctlySetsAndGetsLastName() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setLastName("Doe");
        assertEquals("Doe", applicant.getLastName());
    }

    @Test
    void correctlySetsAndGetsMiddleName() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setMiddleName("Michael");
        assertEquals("Michael", applicant.getMiddleName());
    }

    @Test
    void correctlySetsAndGetsCorrespondenceAddress() {
        AddressDao address = new AddressDao();
        address.setAddressLine1("123 Main St");
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setCorrespondenceAddress(address);
        assertEquals(address, applicant.getCorrespondenceAddress());
    }

    @Test
    void correctlySetsAndGetsDateOfBirth() {
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setDateOfBirth(dateOfBirth);
        assertEquals(dateOfBirth, applicant.getDateOfBirth());
    }

    @Test
    void correctlySetsAndGetsNationality() {
        NationalityDao nationality = new NationalityDao();
        nationality.setFirstNationality("British");
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setNationality(nationality);
        assertEquals(nationality, applicant.getNationality());
    }

    @Test
    void correctlySetsAndGetsCountryOfResidence() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setCountryOfResidence("United Kingdom");
        assertEquals("United Kingdom", applicant.getCountryOfResidence());
    }

    @Test
    void correctlySetsAndGetsCorrespondenceAddressIsSameAsRegisteredOfficeAddress() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setCorrespondenceAddressIsSameAsRegisteredOfficeAddress(true);
        assertTrue(applicant.getCorrespondenceAddressIsSameAsRegisteredOfficeAddress());
    }

    @Test
    void correctlySetsAndGetsCorrespondenceEmail() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        applicant.setCorrespondenceEmail("test@example.com");
        assertEquals("test@example.com", applicant.getCorrespondenceEmail());
    }

    @Test
    void returnsNullForUnsetFields() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        assertNull(applicant.getFirstName());
        assertNull(applicant.getLastName());
        assertNull(applicant.getMiddleName());
        assertNull(applicant.getCorrespondenceAddress());
        assertNull(applicant.getDateOfBirth());
        assertNull(applicant.getNationality());
        assertNull(applicant.getCountryOfResidence());
        assertNull(applicant.getCorrespondenceEmail());
    }

    @Test
    void returnsFalseForUnsetBooleanField() {
        ApplicantDetailsDao applicant = new ApplicantDetailsDao();
        assertFalse(applicant.getCorrespondenceAddressIsSameAsRegisteredOfficeAddress());
    }
}