package uk.gov.companieshouse.acsp.mapper;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.acsp.models.dao.AMLSupervisoryBodiesDao;
import uk.gov.companieshouse.acsp.models.dto.AMLSupervisoryBodiesDto;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.RoleType;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;

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

    @Test
    void AmlSupervisoryBodiesNameToLabel() {
        AMLSupervisoryBodiesDto[] nameArray = new AMLSupervisoryBodiesDto[ ]{
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.ATT.getName(),"123", null),
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.FA.getName(),"345", null)
        };

        AMLSupervisoryBodiesDao[] expectedArray = new AMLSupervisoryBodiesDao[ ]{
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.ATT.getLabel(),"123"),
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.FA.getLabel(),"345")
        };

        AMLSupervisoryBodiesDao[] actualArray = enumTranslator.amlSupervisoryBodiesNameToLabel(nameArray);

        assertEquals(expectedArray[0].getAmlSupervisoryBody(), actualArray[0].getAmlSupervisoryBody());
        assertEquals(expectedArray[0].getMembershipId(), actualArray[0].getMembershipId());
        assertEquals(expectedArray[1].getAmlSupervisoryBody(), actualArray[1].getAmlSupervisoryBody());
        assertEquals(expectedArray[1].getMembershipId(), actualArray[1].getMembershipId());
    }

    @Test
    void AmlSupervisoryBodiesLabelToName() {
        AMLSupervisoryBodiesDao[] labelArray = new AMLSupervisoryBodiesDao[ ]{
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.ATT.getLabel(),"123"),
                new AMLSupervisoryBodiesDao(AMLSupervisoryBodies.FA.getLabel(),"345")
        };

        AMLSupervisoryBodiesDto[] expectedArray = new AMLSupervisoryBodiesDto[ ]{
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.ATT.getName(),"123", "ATT"),
                new AMLSupervisoryBodiesDto(AMLSupervisoryBodies.FA.getName(),"345", "FA")
        };

        AMLSupervisoryBodiesDto[] actualArray = enumTranslator.amlSupervisoryBodiesLabelToName(labelArray);

        assertEquals(expectedArray[0].getAmlSupervisoryBody(), actualArray[0].getAmlSupervisoryBody());
        assertEquals(expectedArray[0].getMembershipId(), actualArray[0].getMembershipId());
        assertEquals(expectedArray[0].getAmlAcronym(), actualArray[0].getAmlAcronym());
        assertEquals(expectedArray[1].getAmlSupervisoryBody(), actualArray[1].getAmlSupervisoryBody());
        assertEquals(expectedArray[1].getMembershipId(), actualArray[1].getMembershipId());
        assertEquals(expectedArray[1].getAmlAcronym(), actualArray[1].getAmlAcronym());
    }
}



