package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.dto.CompanyDto;
import uk.gov.companieshouse.acsp.models.dto.NationalityDto;
import uk.gov.companieshouse.acsp.models.dto.AMLSupervisoryBodiesDto;
import uk.gov.companieshouse.acsp.models.dto.AcspDataSubmissionDto;
import uk.gov.companieshouse.acsp.models.dto.AddressDto;
import uk.gov.companieshouse.acsp.models.dto.ApplicantDetailsDto;
import uk.gov.companieshouse.acsp.models.enums.AMLSupervisoryBodies;
import uk.gov.companieshouse.acsp.models.enums.AcspType;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.filing.Aml;
import uk.gov.companieshouse.acsp.models.filing.Presenter;
import uk.gov.companieshouse.acsp.models.filing.STPersonalInformation;
import uk.gov.companieshouse.acsp.models.filing.ServiceAddress;
import uk.gov.companieshouse.acsp.models.filing.PersonName;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.payment.PaymentResourceHandler;
import uk.gov.companieshouse.api.handler.payment.request.PaymentGet;
import uk.gov.companieshouse.api.handler.transaction.TransactionsResourceHandler;
import uk.gov.companieshouse.api.handler.transaction.request.TransactionsPaymentGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionLinks;
import uk.gov.companieshouse.api.model.transaction.TransactionPayment;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class FilingServiceTest {

    private static final String ACSP_ID = "abc123";
    private static final String PASS_THROUGH_HEADER = "545345345";
    private static final String FIRST_NAME = "firstName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String LAST_NAME = "lastName";
    private static final String PAYMENT_METHOD = "credit-card";
    private static final String PAYMENT_REFERENCE = "PAYMENT_REFERENCE";
    private static final String COUNTRY_OF_RESIDENCE = "United Kingdom";
    private static final String EMAIL_ADDRESS = "testEmail@email.com";

    private static final String TRANSACTION_ID = "3324324324-3243243-32424";

    private FilingsService filingsService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private AcspService acspService;

    @Mock
    private ApiClient apiClient;

    @Mock
    private TransactionsResourceHandler transactionsResourceHandler;

    @Mock
    private TransactionsPaymentGet transactionsPaymentGet;

    @Mock
    private PaymentGet paymentGet;

    @Mock
    private PaymentResourceHandler paymentResourceHandler;

    private Transaction transaction;
    AcspDataDto acspDataDto;
    AddressDto correspondenceAddress;
    AddressDto businessAddress;

    @BeforeEach
    void init() {
        transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);
        transaction.setStatus(TransactionStatus.CLOSED);
        var transactionLinks = new TransactionLinks();
        transactionLinks.setPayment("/12345678/payment");
        transaction.setLinks(transactionLinks);
        filingsService = new FilingsService(transactionService, acspService, apiClientService);
    }

    private void setACSPDataDto() {
        acspDataDto = new AcspDataDto();
        acspDataDto.setId(ACSP_ID);

        acspDataDto.setApplicantDetails(new ApplicantDetailsDto());

        acspDataDto.getApplicantDetails().setFirstName(FIRST_NAME);
        acspDataDto.getApplicantDetails().setLastName(LAST_NAME);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddress());
        acspDataDto.getApplicantDetails().setCountryOfResidence("United Kingdom");
        acspDataDto.getApplicantDetails().setCorrespondenceEmail("test@email.com");

        NationalityDto nationalityDto = new NationalityDto();
        nationalityDto.setFirstNationality("British");
        nationalityDto.setSecondNationality("Irish");
        nationalityDto.setThirdNationality("Canadian");
        acspDataDto.getApplicantDetails().setNationality(nationalityDto);

        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        AcspDataSubmissionDto dataSubmissionDto = new AcspDataSubmissionDto();
        dataSubmissionDto.setUpdatedAt(LocalDateTime.now());
        acspDataDto.setAcspDataSubmission(dataSubmissionDto);
        ReflectionTestUtils.setField(filingsService, "filingDescriptionIdentifier", "**ACSP Application** submission made");
        ReflectionTestUtils.setField(filingsService, "filingDescription", "acsp application made on {date}");
        ReflectionTestUtils.setField(filingsService, "costAmount", "100");
    }

    private void setACSPDataDtoWithCompanyDetails() {
        setACSPDataDto();
        CompanyDto companyDto = new CompanyDto();
        companyDto.setCompanyName("Company");
        companyDto.setCompanyNumber("12345678");
        acspDataDto.setCompanyDetails(companyDto);
    }

    private void setACSPDataDtoWithCompanyDetailsButNoCompanyNumberAndName() {
        setACSPDataDto();
        CompanyDto companyDto = new CompanyDto();
        acspDataDto.setCompanyDetails(companyDto);
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
    }

    private void setACSPDataDtoWithoutNamesandId() {
        setACSPDataDto();
        acspDataDto.getApplicantDetails().setFirstName(null);
        acspDataDto.getApplicantDetails().setLastName(null);
        acspDataDto.setId(null);
        ReflectionTestUtils.setField(filingsService,
                "filingDescriptionIdentifier",null);
        ReflectionTestUtils.setField(filingsService,
                "filingDescription",null);
    }

    private void setACSPDataDtoWithoutApplicantDetails() {
        setACSPDataDto();
        acspDataDto.setApplicantDetails(null);
        ReflectionTestUtils.setField(filingsService,
                "filingDescriptionIdentifier",null);
        ReflectionTestUtils.setField(filingsService,
                "filingDescription",null);
    }

    void initTransactionPaymentLinkMocks() throws IOException, URIValidationException {
        var transactionPayment = new TransactionPayment();
        transactionPayment.setPaymentReference(PAYMENT_REFERENCE);

        var transactionApiResponse = new ApiResponse<>(200, null, transactionPayment);

        when(apiClientService.getApiClient(PASS_THROUGH_HEADER)).thenReturn(apiClient);
        when(apiClient.transactions()).thenReturn(transactionsResourceHandler);
        when(transactionsResourceHandler.getPayment(anyString())).thenReturn(transactionsPaymentGet);
        when(transactionsPaymentGet.execute()).thenReturn(transactionApiResponse);
    }

    void initGetPaymentMocks() throws ApiErrorResponseException, URIValidationException {
        var paymentApi = new PaymentApi();
        paymentApi.setPaymentMethod(PAYMENT_METHOD);

        var paymentApiResponse = new ApiResponse<>(200, null, paymentApi);

        when(apiClient.payment()).thenReturn(paymentResourceHandler);
        when(paymentResourceHandler.get(anyString())).thenReturn(paymentGet);
        when(paymentGet.execute()).thenReturn(paymentApiResponse);
    }
    private AddressDto buildCorrespondenceAddress() {
        correspondenceAddress = new AddressDto();
        correspondenceAddress.setCountry("Country");
        correspondenceAddress.setRegion("County");
        correspondenceAddress.setAddressLine1("Line1");
        correspondenceAddress.setPostalCode("postcode1");
        correspondenceAddress.setLocality("town");
        correspondenceAddress.setAddressLine2("line2");
        correspondenceAddress.setPremises("propertyDetails");
        return correspondenceAddress;
    }

    private AddressDto buildBusinessAddress() {
        businessAddress = new AddressDto();
        businessAddress.setCountry("Country");
        businessAddress.setRegion("County");
        businessAddress.setAddressLine1("Line1");
        businessAddress.setPostalCode("postcode");
        businessAddress.setLocality("town");
        businessAddress.setAddressLine2("line2");
        businessAddress.setPremises("propertyDetails");
        return businessAddress;
    }

    private AddressDto buildBusinessAddressWithOnlyCountry() {
        businessAddress = new AddressDto();
        businessAddress.setCountry("Country");
        return businessAddress;
    }
    private AddressDto buildCorrespondenceAddressWithOnlyCountry() {
        correspondenceAddress = new AddressDto();
        correspondenceAddress.setCountry("Country");
        return correspondenceAddress;
    }

    private AddressDto buildBlankCorrespondenceAddress() {
        correspondenceAddress = new AddressDto();
        return correspondenceAddress;
    }

    private AddressDto buildBlankBusinessAddress() {
        businessAddress = new AddressDto();
        return businessAddress;
    }

    @Test
    void tesGenerateAcspApplicationFiling() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp", response.getKind());
        Assertions.assertNull(response.getData().get("company_number"));
        Assertions.assertNull(response.getData().get("company_name"));

    }

    @Test
    void tesGenerateAcspApplicationFilingNullApplicantDetails() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithoutApplicantDetails();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertNull(((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertNull(((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp", response.getKind());
    }

    @Test
    void tesGenerateAcspApplicationFilingWithNoCorrespondenAddress() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(null);
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertNotNull(response.getData().get("service_address"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp", response.getKind());

    }


    @Test
    void tesGenerateAcspApplicationFilingWithOnlyCountryCorrespondenAddress() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddressWithOnlyCountry());
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals("COUNTRY", ((ServiceAddress) response.getData().get("service_address")).getCorrespondenceAddress().getCountry());
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp", response.getKind());

    }

    @Test
    void tesGenerateAcspApplicationFilingWithBlankAddresses() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildBlankCorrespondenceAddress());
        acspDataDto.setRegisteredOfficeAddress(buildBlankBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNull(response.getData().get("registered_office_address"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp", response.getKind());

    }

    @Test
    void tesGenerateAcspApplicationFilingWithCompanyDetails() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithCompanyDetails();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LC);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildBlankCorrespondenceAddress());
        acspDataDto.setRegisteredOfficeAddress(buildBlankBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp", response.getKind());
        Assertions.assertEquals("COMPANY", response.getData().get("company_name"));
        Assertions.assertNotEquals("company", response.getData().get("company_name"));
        Assertions.assertEquals("12345678", response.getData().get("company_number"));
    }

    @Test
    void tesGenerateAcspApplicationFilingWithCompanyDetailsForCorporateBody() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithCompanyDetails();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.CORPORATE_BODY);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildBlankCorrespondenceAddress());
        acspDataDto.setRegisteredOfficeAddress(buildBlankBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp", response.getKind());
        Assertions.assertEquals("COMPANY", response.getData().get("company_name"));
        Assertions.assertNotEquals("company", response.getData().get("company_name"));
        Assertions.assertEquals("12345678", response.getData().get("company_number"));
    }

    @Test
    void tesGenerateAcspApplicationFilingWithNoNamesAndId() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithoutNamesandId();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildBlankCorrespondenceAddress());
        acspDataDto.setRegisteredOfficeAddress(buildBlankBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertNull(((Presenter)response.getData().get("presenter")).getFirstName());
        Assertions.assertNull(((Presenter)response.getData().get("presenter")).getLastName());
        Assertions.assertNull(((Presenter)response.getData().get("presenter")).getUserId());
        Assertions.assertNull(response.getDescriptionIdentifier());
        Assertions.assertNull(response.getDescription());
    }


    @Test
    void tesGenerateAcspApplicationFilingWithCompanyDetailsWithoutCompanyNameAndNumber() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithCompanyDetailsButNoCompanyNumberAndName();
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull(response.getData().get("company_name"));
        Assertions.assertNull(response.getData().get("company_number"));
    }


    @Test
    void tesGenerateAcspApplicationAllData() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LC);
        acspDataDto.setWorkSector(BusinessSector.AIP);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals(response.getData().get("acsp_type").toString().toUpperCase(),
                TypeOfBusiness.LC.name().toUpperCase());
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertEquals(BusinessSector.AIP.toString(), response.getData().get("business_sector"));
        Assertions.assertEquals("CREDIT-CARD", response.getData().get("payment_method"));
        Assertions.assertEquals("PAYMENT_REFERENCE", response.getData().get("payment_reference"));
        Assertions.assertNull(response.getData().get("business_name"));
        Arrays.stream(((Aml)response.getData().get("aml")).getAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("12345678", amlMembership.getRegistrationNumber().toUpperCase());
                    Assertions.assertEquals("HMRC", amlMembership.getSupervisoryBody().toUpperCase());
                }
        );
        Assertions.assertNull(response.getData().get("company_number"));
        Assertions.assertEquals("TEST@EMAIL.COM", response.getData().get("email"));
    }


    @Test
    void tesGenerateAcspApplicationForSoleTrader() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.ILP);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull( response.getData().get("business_name"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(response.getData().get("service_address"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(),
                ((STPersonalInformation)response.getData().get("st_personal_information")).getPersonName().getFirstName());
        Assertions.assertEquals("1984-10-31",
                ((STPersonalInformation)response.getData().get("st_personal_information")).getBirthDate());
        Assertions.assertEquals("BRITISH,IRISH,CANADIAN",
                ((STPersonalInformation)response.getData().get("st_personal_information")).getNationalityOther());
        Assertions.assertEquals(COUNTRY_OF_RESIDENCE.toUpperCase(),
                ((STPersonalInformation)response.getData().get("st_personal_information")).getUsualResidence());
    }

    @Test
    void tesGenerateAcspApplicationForSoleTraderNoOfficersName() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.TCSP);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        NationalityDto nationalityDto = new NationalityDto();
        acspDataDto.getApplicantDetails().setNationality(nationalityDto);
        acspDataDto.getApplicantDetails().setFirstName(null);
        acspDataDto.getApplicantDetails().setLastName(null);
        acspDataDto.getApplicantDetails().setMiddleName(null);
        acspDataDto.getApplicantDetails().setDateOfBirth(null);
        acspDataDto.getApplicantDetails().setCountryOfResidence(null);

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull( response.getData().get("business_name"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(response.getData().get("service_address"));
        Assertions.assertEquals("HMRC" , Arrays.stream(((Aml)response.getData().get("aml")).getAmlMemberships()).findFirst().get().getSupervisoryBody());
        Assertions.assertNull(((STPersonalInformation)response.getData().get("st_personal_information")).getBirthDate());
        Assertions.assertNull(((STPersonalInformation)response.getData().get("st_personal_information")).getUsualResidence());
    }

    @Test
    void tesGenerateAcspApplicationForSoleTraderNullNationality() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.TCSP);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        acspDataDto.getApplicantDetails().setCountryOfResidence(null);
        acspDataDto.getApplicantDetails().getNationality().setThirdNationality("");
        acspDataDto.getApplicantDetails().getNationality().setSecondNationality("");

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("businessName".toUpperCase(), response.getData().get("business_name"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(response.getData().get("service_address"));
        Assertions.assertEquals("HMRC" , Arrays.stream(((Aml)response.getData().get("aml")).getAmlMemberships()).findFirst().get().getSupervisoryBody());
        Assertions.assertEquals(FIRST_NAME.toUpperCase(),
                ((STPersonalInformation)response.getData().get("st_personal_information")).getPersonName().getFirstName());
        Assertions.assertEquals("1984-10-31",
                ((STPersonalInformation)response.getData().get("st_personal_information")).getBirthDate());
        Assertions.assertNull(((STPersonalInformation)response.getData().get("st_personal_information")).getUsualResidence());
        Assertions.assertEquals("BRITISH",
                ((STPersonalInformation)response.getData().get("st_personal_information")).getNationalityOther());
    }


    @Test
    void testGenerateAcspApplicationBusinessAddress() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.ILP);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        acspDataDto.getApplicantDetails().getNationality().setThirdNationality("");
        acspDataDto.getApplicantDetails().getNationality().setSecondNationality("");
        acspDataDto.getApplicantDetails().getNationality().setFirstNationality("");

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LP);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("HMRC" , Arrays.stream(((Aml)response.getData().get("aml")).getAmlMemberships()).findFirst().get().getSupervisoryBody());
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(response.getData().get("st_personal_information"));

    }
    @Test
    void tesGenerateAcspApplicationBusineessAddressForPatnerships() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.HVD);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.PARTNERSHIP);
        var response2 = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(response2.getData().get("registered_office_address"));

    }
    @Test
    void tesGenerateAcspApplicationBusineessAddressForUnincorporatedEntity() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.CASINOS);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.UNINCORPORATED);
        var response2 = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(response2.getData().get("registered_office_address"));

    }

    @Test
    void tesGenerateAcspApplicationBusinessAddressWithOnlyCountry() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.PNTS);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddressWithOnlyCountry());


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LP);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertEquals("COUNTRY", ((uk.gov.companieshouse.acsp.models.filing.Address)response.getData().get("registered_office_address")).getCountry());

    }

    @Test
    void tesGenerateAcspApplicationServiceROATrue() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.CI);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.getApplicantDetails().setFirstName(null);
        acspDataDto.getApplicantDetails().setLastName(null);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        acspDataDto.getApplicantDetails().setCorrespondenceAddressIsSameAsRegisteredOfficeAddress(true);
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddressWithOnlyCountry());
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddressWithOnlyCountry());

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LP);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertTrue(((ServiceAddress)(response.getData().get("service_address"))).getIsServiceAddressROA());
    }


    @Test
    void tesGenerateAcspApplicationServiceROAFalse() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        acspDataDto.setWorkSector(BusinessSector.AIP);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.getApplicantDetails().setDateOfBirth(localDate);
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddressWithOnlyCountry());
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddress());

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LP);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertFalse(((ServiceAddress)(response.getData().get("service_address"))).getIsServiceAddressROA());
    }

    @Test
    void tesDescriptionDate() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);
        transaction.setClosedAt("2024-07-01T12:53Z");

        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.REGISTER_ACSP);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertTrue(response.getDescription().contains("01-07-2024"));
    }

    @Test
    void testGenerateAcspApplicationFilingUpdateAcsp() throws Exception {
        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.UPDATE_ACSP);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull(response.getCost());
        Assertions.assertNull(response.getData().get("payment_reference"));
        Assertions.assertNull(response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertEquals("acsp#update", response.getKind());
    }

    @Test
    void testGenerateAllDataUpdateLimitedAcsp() throws Exception {
        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.UPDATE_ACSP);
        acspDataDto.setAcspId(ACSP_ID);
        acspDataDto.getApplicantDetails().setCorrespondenceEmail(EMAIL_ADDRESS);
        acspDataDto.setBusinessName("Test Limited Company Name Update ACSP");
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddress());
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LC);

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);

        AMLSupervisoryBodiesDto amlSupervisoryBodies2 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies2.setAmlSupervisoryBody(AMLSupervisoryBodies.FCA);
        amlSupervisoryBodies2.setMembershipId("87654321");
        AMLSupervisoryBodiesDto[] removedAmlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies2};
        acspDataDto.setRemovedAmlSupervisoryBodies(removedAmlSupervisoryBodies);

        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);

        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(EMAIL_ADDRESS.toUpperCase(), response.getData().get("email"));
        Assertions.assertEquals("Test Limited Company Name Update ACSP".toUpperCase(), response.getData().get("proposed_corporate_body_name"));
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNotNull(response.getData().get("service_address"));
        Assertions.assertNull(response.getData().get("st_personal_information"));

        Aml acspDetails = (Aml) response.getData().get("aml");
        Assertions.assertNotNull(acspDetails.getAmlMemberships());
        Assertions.assertEquals(1, acspDetails.getAmlMemberships().length);
        Arrays.stream(acspDetails.getAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("12345678", amlMembership.getRegistrationNumber());
                    Assertions.assertEquals(AMLSupervisoryBodies.HMRC.name(), amlMembership.getSupervisoryBody().toUpperCase());
                }
        );

        Assertions.assertNotNull(acspDetails.getPreviousAmlMemberships());
        Assertions.assertEquals(1, acspDetails.getPreviousAmlMemberships().length);
        Arrays.stream(acspDetails.getPreviousAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("87654321", amlMembership.getRegistrationNumber());
                    Assertions.assertEquals(AMLSupervisoryBodies.FCA.name(), amlMembership.getSupervisoryBody().toUpperCase());
                }
        );
    }

    @Test
    void testGenerateAllDataUpdateLLPAcsp() throws Exception {
        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.UPDATE_ACSP);
        acspDataDto.setAcspId(ACSP_ID);
        acspDataDto.getApplicantDetails().setCorrespondenceEmail(EMAIL_ADDRESS);
        acspDataDto.setBusinessName("Test LLP Company Name Update ACSP");
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddress());
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LLP);

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.HMRC);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);

        AMLSupervisoryBodiesDto amlSupervisoryBodies2 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies2.setAmlSupervisoryBody(AMLSupervisoryBodies.FCA);
        amlSupervisoryBodies2.setMembershipId("87654321");
        AMLSupervisoryBodiesDto[] removedAmlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies2};
        acspDataDto.setRemovedAmlSupervisoryBodies(removedAmlSupervisoryBodies);

        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);

        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(EMAIL_ADDRESS.toUpperCase(), response.getData().get("email"));
        Assertions.assertEquals("Test LLP Company Name Update ACSP".toUpperCase(), response.getData().get("proposed_corporate_body_name"));
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNotNull(response.getData().get("service_address"));
        Assertions.assertNull(response.getData().get("st_personal_information"));

        Aml acspDetails = (Aml) response.getData().get("aml");
        Assertions.assertNotNull(acspDetails.getAmlMemberships());
        Assertions.assertEquals(1, acspDetails.getAmlMemberships().length);
        Arrays.stream(acspDetails.getAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("12345678", amlMembership.getRegistrationNumber());
                    Assertions.assertEquals(AMLSupervisoryBodies.HMRC.name(), amlMembership.getSupervisoryBody().toUpperCase());
                }
        );

        Assertions.assertNotNull(acspDetails.getPreviousAmlMemberships());
        Assertions.assertEquals(1, acspDetails.getPreviousAmlMemberships().length);
        Arrays.stream(acspDetails.getPreviousAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("87654321", amlMembership.getRegistrationNumber());
                    Assertions.assertEquals(AMLSupervisoryBodies.FCA.name(), amlMembership.getSupervisoryBody().toUpperCase());
                }
        );
    }

    @Test
    void testGenerateAllDataUpdateSoleTraderAcsp() throws Exception {
        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.UPDATE_ACSP);
        acspDataDto.setAcspId(ACSP_ID);
        acspDataDto.getApplicantDetails().setCorrespondenceEmail(EMAIL_ADDRESS);
        acspDataDto.setBusinessName("Test Sole Trader Business Name Update ACSP");
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddress());
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        acspDataDto.getApplicantDetails().setFirstName(FIRST_NAME);
        acspDataDto.getApplicantDetails().setMiddleName(MIDDLE_NAME);
        acspDataDto.getApplicantDetails().setLastName(LAST_NAME);

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody(AMLSupervisoryBodies.AAT);
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);

        AMLSupervisoryBodiesDto amlSupervisoryBodies2 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies2.setAmlSupervisoryBody(AMLSupervisoryBodies.AIA);
        amlSupervisoryBodies2.setMembershipId("87654321");
        AMLSupervisoryBodiesDto[] removedAmlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies2};
        acspDataDto.setRemovedAmlSupervisoryBodies(removedAmlSupervisoryBodies);

        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);

        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(EMAIL_ADDRESS.toUpperCase(), response.getData().get("email"));
        Assertions.assertEquals("Test Sole Trader Business Name Update ACSP".toUpperCase(), response.getData().get("proposed_corporate_body_name"));
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNotNull(response.getData().get("service_address"));
        Assertions.assertNotNull(response.getData().get("st_personal_information"));


        Aml acspDetails = (Aml) response.getData().get("aml");
        Assertions.assertNotNull(acspDetails.getAmlMemberships());
        Assertions.assertEquals(1, acspDetails.getAmlMemberships().length);
        Arrays.stream(acspDetails.getAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("12345678", amlMembership.getRegistrationNumber());
                    Assertions.assertEquals(AMLSupervisoryBodies.AAT.name(), amlMembership.getSupervisoryBody().toUpperCase());
                }
        );
        Assertions.assertNotNull(acspDetails.getPreviousAmlMemberships());
        Assertions.assertEquals(1, acspDetails.getPreviousAmlMemberships().length);
        Arrays.stream(acspDetails.getPreviousAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("87654321", amlMembership.getRegistrationNumber());
                    Assertions.assertEquals(AMLSupervisoryBodies.AIA.name(), amlMembership.getSupervisoryBody().toUpperCase());
                }
        );

        PersonName personName = acspDetails.getPersonName();
        Assertions.assertNotNull(personName);
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), personName.getFirstName());
        Assertions.assertEquals(MIDDLE_NAME.toUpperCase(), personName.getMiddleName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), personName.getLastName());
    }

    @Test
    void testGenerateUpdateCorporateBodyAcspWithNullCorrespondenceAddress() throws Exception {
        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.UPDATE_ACSP);
        acspDataDto.setAcspId(ACSP_ID);
        acspDataDto.getApplicantDetails().setCorrespondenceEmail(EMAIL_ADDRESS);
        acspDataDto.setBusinessName("Test Corporate Body Name Update ACSP");
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(null);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.CORPORATE_BODY);

        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);

        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(EMAIL_ADDRESS.toUpperCase(), response.getData().get("email"));
        Assertions.assertEquals("Test Corporate Body Name Update ACSP".toUpperCase(), response.getData().get("proposed_corporate_body_name"));
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(response.getData().get("service_address"));
        Assertions.assertNull(response.getData().get("st_personal_information"));
    }

    @Test
    void testBuildUpdateAcspDataWithNonNullData() throws Exception {
        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.UPDATE_ACSP);
        acspDataDto.setAcspId(ACSP_ID);
        acspDataDto.getApplicantDetails().setCorrespondenceEmail(EMAIL_ADDRESS);
        acspDataDto.setBusinessName("Test Business Name Update ACSP");
        acspDataDto.setRegisteredOfficeAddress(buildBusinessAddress());
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(buildCorrespondenceAddress());
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);

        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);

        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(EMAIL_ADDRESS.toUpperCase(), response.getData().get("email"));
        Assertions.assertEquals("Test Business Name Update ACSP".toUpperCase(), response.getData().get("proposed_corporate_body_name"));
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNotNull(response.getData().get("service_address"));
        Assertions.assertNotNull(response.getData().get("st_personal_information"));
    }

    @Test
    void testBuildUpdateAcspDataWithNullValues() throws Exception {
        setACSPDataDto();
        acspDataDto.setAcspType(AcspType.UPDATE_ACSP);
        acspDataDto.setAcspId(ACSP_ID);
        acspDataDto.getApplicantDetails().setCorrespondenceEmail(null);
        acspDataDto.setBusinessName(null);
        acspDataDto.setRegisteredOfficeAddress(null);
        acspDataDto.getApplicantDetails().setCorrespondenceAddress(null);
        acspDataDto.setTypeOfBusiness(null);

        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);

        Assertions.assertNotNull(response.getData());
        Assertions.assertNull(response.getData().get("email"));
        Assertions.assertNull(response.getData().get("proposed_corporate_body_name"));
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(response.getData().get("service_address"));
        Assertions.assertNull(response.getData().get("st_personal_information"));
    }
}