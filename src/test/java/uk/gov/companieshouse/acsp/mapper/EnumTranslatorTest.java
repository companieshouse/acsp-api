package uk.gov.companieshouse.acsp.mapper;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.models.dao.AMLSupervisoryBodiesDao;
import uk.gov.companieshouse.acsp.models.dto.AMLSupervisoryBodiesDto;
import uk.gov.companieshouse.acsp.models.enums.*;

@ExtendWith(MockitoExtension.class)
 class EnumTranslatorTest {
    @InjectMocks
    private EnumTranslator enumTranslator;

    @Test
    void typeOfBusinessEnumToString() {
        TypeOfBusiness inputEnum = TypeOfBusiness.PARTNERSHIP;
        String expected = "non-registered-partnership";
        String actual = enumTranslator.typeOfBusinessEnumToString(inputEnum);

        assertEquals(expected, actual);
    }
    @Test
    void typeOfBusinessEnumToStringNull() {
        String expected = "";
        String actual = enumTranslator.typeOfBusinessEnumToString(null);

        assertEquals(expected, actual);
    }

    @Test
    void typeOfBusinessStringToEnum() {
        String inputEnum = TypeOfBusiness.LC.label;
        TypeOfBusiness expected = TypeOfBusiness.LC;
        TypeOfBusiness actual = enumTranslator.typeOfBusinessStringToEnum(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void workSectorEnumToString() {
        BusinessSector inputEnum = BusinessSector.CI;
        String expected = "credit-institutions";
        String actual = enumTranslator.businessSectorEnumToString(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void workSectorEnumToNullString() {
        String expected = "";
        String actual = enumTranslator.businessSectorEnumToString(null);

        assertEquals(expected, actual);
    }
    @Test
    void workSectorStringToEnum() {
        String inputEnum = BusinessSector.AIP.label;
        BusinessSector expected = BusinessSector.AIP;
        BusinessSector actual = enumTranslator.businessSectorStringToEnum(inputEnum);

        assertEquals(expected, actual);
    }
    @Test
    void roleTypeEnumToString() {
        RoleType inputEnum = RoleType.GENERAL_PARTNER;
        String expected = "general-partner";
        String actual = enumTranslator.roleTypeEnumToString(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void roleTypeEnumToNullString() {
        String expected = "";
        String actual = enumTranslator.roleTypeEnumToString(null);

        assertEquals(expected, actual);
    }
    @Test
    void roleTypeStringToEnum() {
        String inputEnum = RoleType.DIRECTOR.label;
        RoleType expected = RoleType.DIRECTOR;
        RoleType actual = enumTranslator.roleTypeStringToEnum(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void AmlSupervisoryBodiesEnumToString() {
        AMLSupervisoryBodiesDto[] inputEnumArray = new AMLSupervisoryBodiesDto[ ]{
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.AAT,"123"),
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.FA,"345")
        };

        AMLSupervisoryBodiesDao[] expectedArray = new AMLSupervisoryBodiesDao[ ]{
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.AAT.getLabel(),"123"),
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.FA.getLabel(),"345")
        };

        AMLSupervisoryBodiesDao[] actualArray = enumTranslator.amlSupervisoryBodiesEnumToString(inputEnumArray);

        assertEquals(expectedArray[0].getAmlSupervisoryBody(), actualArray[0].getAmlSupervisoryBody());
        assertEquals(expectedArray[0].getMembershipId(), actualArray[0].getMembershipId());
        assertEquals(expectedArray[1].getAmlSupervisoryBody(), actualArray[1].getAmlSupervisoryBody());
        assertEquals(expectedArray[1].getMembershipId(), actualArray[1].getMembershipId());
    }

    @Test
    void AmlSupervisoryBodiesEnumToStringForNullInputArray() {
        AMLSupervisoryBodiesDto[] inputEnumArray = new AMLSupervisoryBodiesDto[ ]{ };
        AMLSupervisoryBodiesDao[] expectedArray = new AMLSupervisoryBodiesDao[0];
        AMLSupervisoryBodiesDao[] actualArray = enumTranslator.amlSupervisoryBodiesEnumToString(inputEnumArray);
        assertArrayEquals(expectedArray,actualArray);
    }

    @Test
    void AmlSupervisoryBodiesStringToEnum() {
        AMLSupervisoryBodiesDao[] inputStrArray = new AMLSupervisoryBodiesDao[ ]{
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.ATT.getLabel(),"123"),
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.FA.getLabel(),"345")
        };

        AMLSupervisoryBodiesDto[] expectedArray = new AMLSupervisoryBodiesDto[ ]{
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.ATT,"123"),
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.FA,"345")
        };

        AMLSupervisoryBodiesDto[] actualArray = enumTranslator.amlSupervisoryBodiesStringToEnum(inputStrArray);

        assertEquals(expectedArray[0].getAmlSupervisoryBody(), actualArray[0].getAmlSupervisoryBody());
        assertEquals(expectedArray[0].getMembershipId(), actualArray[0].getMembershipId());
        assertEquals(expectedArray[1].getAmlSupervisoryBody(), actualArray[1].getAmlSupervisoryBody());
        assertEquals(expectedArray[1].getMembershipId(), actualArray[1].getMembershipId());
    }

    @Test
    void AmlSupervisoryBodiesDbValueToDisplayLabelForNullInputArray() {
        AMLSupervisoryBodiesDao[] inputStrArray = new AMLSupervisoryBodiesDao[ ]{ };
        AMLSupervisoryBodiesDto[] expectedArray = new AMLSupervisoryBodiesDto[0];
        AMLSupervisoryBodiesDto[] actualArray = enumTranslator.amlSupervisoryBodiesStringToEnum(inputStrArray);
        assertArrayEquals(expectedArray,actualArray);
    }

    @Test
    void acspTypeEnumToString() {
        AcspType inputEnum = AcspType.REGISTER_ACSP;
        String expected = "register-acsp";
        String actual = enumTranslator.acspTypeEnumToString(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void acspTypeEnumToStringNull() {
        String expected = "";
        String actual = enumTranslator.acspTypeEnumToString(null);

        assertEquals(expected, actual);
    }

    @Test
    void acspTypeStringToEnum() {
        String inputEnum = AcspType.REGISTER_ACSP.label;
        AcspType expected = AcspType.REGISTER_ACSP;
        AcspType actual = enumTranslator.acspTypeStringToEnum(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void applicationTypeEnumToString() {
        ApplicationType inputEnum = ApplicationType.VERIFICATION;
        String expected = "verification";
        String actual = enumTranslator.applicationTypeEnumToString(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void applicationTypeEnumToStringNull() {
        String expected = "";
        String actual = enumTranslator.applicationTypeEnumToString(null);

        assertEquals(expected, actual);
    }

    @Test
    void applicationTypeStringToEnum() {
        String inputEnum = ApplicationType.REVERIFICATION.label;
        ApplicationType expected = ApplicationType.REVERIFICATION;
        ApplicationType actual = enumTranslator.applicationTypeStringToEnum(inputEnum);

        assertEquals(expected, actual);
    }

    @Test
    void applicationTypeStringToEnumInvalid() {
        String inputString = "invalid_application_type";
        ApplicationType expected = ApplicationType.VERIFICATION;
        ApplicationType actual = enumTranslator.applicationTypeStringToEnum(inputString);
        assertEquals(expected, actual);
    }
}
