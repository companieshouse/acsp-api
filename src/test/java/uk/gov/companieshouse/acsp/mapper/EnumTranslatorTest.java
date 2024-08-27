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
	public void typeOfBusinessEnum() {
		TypeOfBusiness inputEnum = TypeOfBusiness.PARTNERSHIP;
		String expected = "non-registered-partnership";
		String actual = enumTranslator.typeOfBusinessEnumToString(inputEnum);

		assertEquals(expected, actual);
	}

	@Test
	public void roleTypeEnum() {
		RoleType inputEnum = RoleType.GENERAL_PARTNER;
		String expected = "general-partner";
		String actual = enumTranslator.roleTypeEnumToString(inputEnum);

		assertEquals(expected, actual);
	}


	@Test
	public void workSectorEnum() {
		BusinessSector inputEnum = BusinessSector.AIA;
		String expected = "auditors, insolvency-practitioners, external-accountants-and-tax-advisers";
		String actual = enumTranslator.businessSectorEnumToString(inputEnum);

		assertEquals(expected, actual);
	}

}
