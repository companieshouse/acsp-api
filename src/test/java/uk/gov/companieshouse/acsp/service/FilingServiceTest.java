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
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.filing.Aml;
import uk.gov.companieshouse.acsp.models.filing.Presenter;
import uk.gov.companieshouse.acsp.models.filing.STPersonalInformation;
import uk.gov.companieshouse.acsp.models.filing.ServiceAddress;
import uk.gov.companieshouse.acsp.models.type.Address;
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

    private static final String REQUEST_ID = "xyz987";
    private static final String ACSP_ID = "abc123";
    private static final String PASS_THROUGH_HEADER = "545345345";
    private static final String FIRST_NAME = "firstName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String LAST_NAME = "lastName";
    private static final String PAYMENT_METHOD = "credit-card";
    private static final String PAYMENT_REFERENCE = "PAYMENT_REFERENCE";
    private static final String COUNTRY_OF_RESIDENCE = "United Kingdom";

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
    AcspDataSubmissionDto dataSubmissionDto;
    AcspDataDto acspDataDto;
    Address correspondenceAddress;
    Address businessAddress;

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
        acspDataDto.setFirstName(FIRST_NAME);
        acspDataDto.setLastName(LAST_NAME);
        acspDataDto.setEmail("email@email.com");
        AcspDataSubmissionDto dataSubmissionDto = new AcspDataSubmissionDto();
        dataSubmissionDto.setUpdatedAt(LocalDateTime.now());
        acspDataDto.setAcspDataSubmission(dataSubmissionDto);
        acspDataDto.setCorrespondenceAddress(buildCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBusinessAddress());
        acspDataDto.setCountryOfResidence("United Kingdom");
        ReflectionTestUtils.setField(filingsService,
                "filingDescriptionIdentifier","**ACSP Application** submission made");
        ReflectionTestUtils.setField(filingsService,
                "filingDescription","acsp application made on {date}");
        ReflectionTestUtils.setField(filingsService,
                "costAmount","100");
        NationalityDto nationalityDto = new NationalityDto();
        nationalityDto.setFirstNationality("British");
        nationalityDto.setThirdNationality("Canadian");
        nationalityDto.setSecondNationality("Irish");
        acspDataDto.setNationality(nationalityDto);
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
    }

    private void setACSPDataDtoWithoutNamesandId() {
        setACSPDataDto();
        acspDataDto.setFirstName(null);
        acspDataDto.setLastName(null);
        acspDataDto.setId(null);
        ReflectionTestUtils.setField(filingsService,
                "filingDescriptionIdentifier",null);
        ReflectionTestUtils.setField(filingsService,
                "filingDescription",null);
    }

    private void setACSPDataDtoWithoutemailaddress() {
        setACSPDataDto();
        acspDataDto.setEmail(null);
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
    private Address buildCorrespondenceAddress() {
        correspondenceAddress = new Address();
        correspondenceAddress.setCountry("Country");
        correspondenceAddress.setCounty("County");
        correspondenceAddress.setLine1("Line1");
        correspondenceAddress.setPostcode("postcode1");
        correspondenceAddress.setTown("town");
        correspondenceAddress.setLine2("line2");
        correspondenceAddress.setPropertyDetails("propertyDetails");
        return correspondenceAddress;
    }

    private Address buildBusinessAddress() {
        businessAddress = new Address();
        businessAddress.setCountry("Country");
        businessAddress.setCounty("County");
        businessAddress.setLine1("Line1");
        businessAddress.setPostcode("postcode");
        businessAddress.setTown("town");
        businessAddress.setLine2("line2");
        businessAddress.setPropertyDetails("propertyDetails");
        return businessAddress;
    }

    private Address buildBusinessAddressWithOnlyCountry() {
        businessAddress = new Address();
        businessAddress.setCountry("Country");
        return businessAddress;
    }
    private Address buildCorrespondenceAddressWithOnlyCountry() {
        correspondenceAddress = new Address();
        correspondenceAddress.setCountry("Country");
        return correspondenceAddress;
    }

    private Address buildBlankCorrespondenceAddress() {
        correspondenceAddress = new Address();
        return correspondenceAddress;
    }

    private Address buildBlankBusinessAddress() {
        businessAddress = new Address();
        return businessAddress;
    }

    @Test
    void tesGenerateAcspApplicationFiling() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
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
    void tesGenerateAcspApplicationFilingWithNoCorrespondenAddress() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setCorrespondenceAddress(null);
        acspDataDto.setBusinessAddress(buildBusinessAddress());
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
        acspDataDto.setCorrespondenceAddress(buildCorrespondenceAddressWithOnlyCountry());
        acspDataDto.setBusinessAddress(buildBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertNotNull(response.getData());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), response.getData().get("payment_reference"));
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), response.getData().get("payment_method"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals("COUNTRY", ((ServiceAddress)response.getData().get("service_address")).getCorrespondenceAddress().getCountry());
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
        acspDataDto.setCorrespondenceAddress(buildBlankCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBlankBusinessAddress());
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
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LC);
        acspDataDto.setCorrespondenceAddress(buildBlankCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBlankBusinessAddress());
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
        acspDataDto.setCorrespondenceAddress(buildBlankCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBlankBusinessAddress());
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
    void tesGenerateAcspApplicationFilingWithNoEmail() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithoutemailaddress();
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull(response.getData().get("email"));
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
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LC);
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals(response.getData().get("acsp_type").toString().toUpperCase(),
                TypeOfBusiness.LC.name().toUpperCase());
        Assertions.assertNotNull(response.getData().get("aml"));
        Assertions.assertEquals("WORK SECTOR", response.getData().get("business_sector"));
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
    }


    @Test
    void tesGenerateAcspApplicationForSoleTrader() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull( response.getData().get("business_name"));
        Assertions.assertNull(response.getData().get("registered_office_address"));
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
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        NationalityDto nationalityDto = new NationalityDto();
        acspDataDto.setNationality(nationalityDto);
        acspDataDto.setFirstName(null);
        acspDataDto.setLastName(null);
        acspDataDto.setMiddleName(null);
        acspDataDto.setDateOfBirth(null);
        acspDataDto.setCountryOfResidence(null);


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull( response.getData().get("business_name"));
        Assertions.assertNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(((STPersonalInformation)response.getData().get("st_personal_information")).getBirthDate());
        Assertions.assertNull(((STPersonalInformation)response.getData().get("st_personal_information")).getUsualResidence());
    }

    @Test
    void tesGenerateAcspApplicationForSoleTraderNullNationality() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        acspDataDto.setCountryOfResidence(null);
        acspDataDto.getNationality().setThirdNationality("");
        acspDataDto.getNationality().setSecondNationality("");


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("businessName".toUpperCase(), response.getData().get("business_name"));
        Assertions.assertNull(response.getData().get("registered_office_address"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(),
                ((STPersonalInformation)response.getData().get("st_personal_information")).getPersonName().getFirstName());
        Assertions.assertEquals("1984-10-31",
                ((STPersonalInformation)response.getData().get("st_personal_information")).getBirthDate());
        Assertions.assertNull(((STPersonalInformation)response.getData().get("st_personal_information")).getUsualResidence());
        Assertions.assertEquals("BRITISH",
                ((STPersonalInformation)response.getData().get("st_personal_information")).getNationalityOther());
    }


    @Test
    void tesGenerateAcspApplicationBusineessAddress() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        acspDataDto.setBusinessAddress(buildBusinessAddress());
        acspDataDto.getNationality().setThirdNationality("");
        acspDataDto.getNationality().setSecondNationality("");
        acspDataDto.getNationality().setFirstNationality("");


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.CORPORATE_BODY);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(response.getData().get("registered_office_address"));
        Assertions.assertNull(response.getData().get("st_personal_information"));

    }
    @Test
    void tesGenerateAcspApplicationBusineessAddressForPatnerships() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        acspDataDto.setBusinessAddress(buildBusinessAddress());

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
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
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        acspDataDto.setBusinessAddress(buildBusinessAddress());


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
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
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        acspDataDto.setBusinessAddress(buildBusinessAddressWithOnlyCountry());


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.CORPORATE_BODY);
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
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setFirstName(null);
        acspDataDto.setLastName(null);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        acspDataDto.setBusinessAddress(buildBusinessAddressWithOnlyCountry());
        acspDataDto.setCorrespondenceAddress(buildCorrespondenceAddressWithOnlyCountry());

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.CORPORATE_BODY);
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
        acspDataDto.setWorkSector("Work Sector");
        acspDataDto.setMiddleName(MIDDLE_NAME);
        acspDataDto.setBusinessName("businessName");
        LocalDate localDate = LocalDate.parse("1984-10-31");
        acspDataDto.setDateOfBirth(localDate);
        acspDataDto.setBusinessAddress(buildBusinessAddressWithOnlyCountry());
        acspDataDto.setCorrespondenceAddress(buildCorrespondenceAddress());

        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.CORPORATE_BODY);
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
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertTrue(response.getDescription().contains("01-07-2024"));

    }
}
