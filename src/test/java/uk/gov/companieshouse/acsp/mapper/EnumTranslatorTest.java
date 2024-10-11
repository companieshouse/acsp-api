package uk.gov.companieshouse.acsp.mapper;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

import java.util.List;

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
        TypeOfBusiness inputEnum = TypeOfBusiness.PARTNERSHIP;
        String expected = "non-registered-partnership";
        String actual = enumTranslator.typeOfBusinessEnumToString(inputEnum);

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
        String inputEnum = BusinessSector.AIA.label;
        BusinessSector expected = BusinessSector.AIA;
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

    // **AML Supervisory Bodies Tests**

    @Test
    void amlSupervisoryBodiesEnumArrayToStringArray() {
        List<AMLSupervisoryBodies> enumList = List.of(
                AMLSupervisoryBodies.ACCA, AMLSupervisoryBodies.AAT, AMLSupervisoryBodies.ATT
        );
        List<String> expected = List.of(
                "association-of-chartered-certified-accountants",
                "association-of-accounting-technicians",
                "association-of-taxation-technicians"
        );
        List<String> actual = enumTranslator.amlSupervisoryBodiesEnumArrayToStringArray(enumList);
        assertEquals(expected, actual);
    }

    @Test
    void stringArrayToAmlSupervisoryBodiesEnumArray() {
        List<String> stringList = List.of(
                "association-of-chartered-certified-accountants",
                "association-of-accounting-technicians",
                "association-of-taxation-technicians"
        );
        List<AMLSupervisoryBodies> expected = List.of(
                AMLSupervisoryBodies.ACCA, AMLSupervisoryBodies.AAT, AMLSupervisoryBodies.ATT
        );
        List<AMLSupervisoryBodies> actual = enumTranslator.stringArrayToAmlSupervisoryBodiesEnumArray(stringList);
        assertEquals(expected, actual);
    }


}


