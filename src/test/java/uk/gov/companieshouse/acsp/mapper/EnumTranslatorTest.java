package uk.gov.companieshouse.acsp.mapper;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

@SpringBootTest
public class EnumTranslatorTest {
    @Autowired
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
    public void workSectorEnumToNullString() {
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
    public void roleTypeEnumToNullString() {
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

}

