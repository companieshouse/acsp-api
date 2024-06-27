package uk.gov.companieshouse.acsp.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.acsp.models.dto.*;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.filing.ACSP;
import uk.gov.companieshouse.acsp.models.filing.Presenter;
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
        acspDataDto.setCorrespondenceAddresses(buildCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBusinessAddress());
        acspDataDto.setCountryOfResidence("United Kingdom");
        ReflectionTestUtils.setField(filingsService,
                "filingDescriptionIdentifier","**ACSP Application** submission made");
        ReflectionTestUtils.setField(filingsService,
                "filingDescription","acsp application made on {date}");
        ReflectionTestUtils.setField(filingsService,
                "costAmount","100");
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
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentReference());
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentMethod());
        Assertions.assertNotNull(response.getData().get("acsp"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp".toUpperCase(), response.getKind());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getCompanyName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getCompanyNumber());

    }

    @Test
    void tesGenerateAcspApplicationFilingWithNoCorrespondenAddress() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setCorrespondenceAddresses(null);
        acspDataDto.setBusinessAddress(buildBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentReference());
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentMethod());
        Assertions.assertNotNull(response.getData().get("acsp"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertNotNull(((ACSP) response.getData().get("acsp")).getCorrespondenceAddress());
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp".toUpperCase(), response.getKind());

    }


    @Test
    void tesGenerateAcspApplicationFilingWithOnlyCountryCorrespondenAddress() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setCorrespondenceAddresses(buildCorrespondenceAddressWithOnlyCountry());
        acspDataDto.setBusinessAddress(buildBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentReference());
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentMethod());
        Assertions.assertNotNull(response.getData().get("acsp"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals("COUNTRY", ((ACSP) response.getData().get("acsp")).getCorrespondenceAddress().getCountry());
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp".toUpperCase(), response.getKind());

    }

    @Test
    void tesGenerateAcspApplicationFilingWithBlankAddresses() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDto();
        acspDataDto.setCorrespondenceAddresses(buildBlankCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBlankBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentReference());
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentMethod());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertNotNull(response.getData().get("acsp"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp".toUpperCase(), response.getKind());

    }

    @Test
    void tesGenerateAcspApplicationFilingWithCompanyDetails() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithCompanyDetails();
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LIMITED_COMPANY);
        acspDataDto.setCorrespondenceAddresses(buildBlankCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBlankBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentReference());
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentMethod());
        Assertions.assertNotNull(response.getData().get("acsp"));
        Assertions.assertNotNull(response.getData().get("presenter"));
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getFirstName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((Presenter) response.getData().get("presenter")).getLastName());
        Assertions.assertNotNull(response.getData().get("submission"));
        Assertions.assertEquals("acsp".toUpperCase(), response.getKind());
        Assertions.assertEquals("COMPANY", ((ACSP) response.getData().get("acsp")).getCompanyName());
        Assertions.assertNotEquals("company", ((ACSP) response.getData().get("acsp")).getCompanyName());
        Assertions.assertEquals("12345678", ((ACSP) response.getData().get("acsp")).getCompanyNumber());
    }


    @Test
    void tesGenerateAcspApplicationFilingWithNoNamesAndId() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithoutNamesandId();
        acspDataDto.setCorrespondenceAddresses(buildBlankCorrespondenceAddress());
        acspDataDto.setBusinessAddress(buildBlankBusinessAddress());
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("100", response.getCost());
        Assertions.assertEquals(PAYMENT_REFERENCE.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentReference());
        Assertions.assertEquals(PAYMENT_METHOD.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPaymentMethod());
        Assertions.assertNotNull(response.getData().get("acsp"));
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
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getEmail());
    }


    @Test
    void tesGenerateAcspApplicationFilingWithCompanyDetailsWithoutCompanyNameAndNumber() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        setACSPDataDtoWithCompanyDetailsButNoCompanyNumberAndName();
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);

        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getCompanyName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getCompanyNumber());
    }


    @Test
    void tesGenerateAcspApplicationAllData() throws Exception {
        initTransactionPaymentLinkMocks();
        initGetPaymentMocks();

        transaction.setStatus(TransactionStatus.CLOSED);

        setACSPDataDto();
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.LIMITED_COMPANY);
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
        Assertions.assertEquals(((ACSP) response.getData().get("acsp")).getAcspType().toUpperCase(),
                TypeOfBusiness.LIMITED_COMPANY.name().toUpperCase());
        Assertions.assertNotNull(((ACSP) response.getData().get("acsp")).getAmlMemberships());
        Assertions.assertEquals("WORK SECTOR", ((ACSP) response.getData().get("acsp")).getBusinessSector());
        Assertions.assertNotNull(((ACSP) response.getData().get("acsp")).getAmlMemberships());
        Assertions.assertEquals("CREDIT-CARD", ((ACSP) response.getData().get("acsp")).getPaymentMethod());
        Assertions.assertEquals("PAYMENT_REFERENCE", ((ACSP) response.getData().get("acsp")).getPaymentReference());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getBusinessName());
        Assertions.assertEquals(FIRST_NAME.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPersonName().getFirstName());
        Assertions.assertEquals(MIDDLE_NAME.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPersonName().getMiddleName());
        Assertions.assertEquals(LAST_NAME.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPersonName().getLastName());
        Arrays.stream(((ACSP) response.getData().get("acsp")).getAmlMemberships()).forEach(
                amlMembership -> {
                    Assertions.assertEquals("12345678", amlMembership.getRegistrationNumber().toUpperCase());
                    Assertions.assertEquals("HMRC", amlMembership.getSupervisoryBody().toUpperCase());
                }
        );
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getCompanyNumber());

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
        NationalityDto nationalityDto = new NationalityDto();
        nationalityDto.setFirstNationality("British");
        nationalityDto.setThirdNationality("Canadian");
        nationalityDto.setSecondNationality("Irish");
        acspDataDto.setNationality(nationalityDto);


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getBusinessName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertEquals(FIRST_NAME.toUpperCase(),
                ((ACSP) response.getData().get("acsp")).getAppointements().getOfficers().getPersonName().getFirstName());
        Assertions.assertEquals("1984-10-31",
                ((ACSP) response.getData().get("acsp")).getAppointements().getOfficers().getBirthDate());
        Assertions.assertEquals("BRITISH,IRISH,CANADIAN",
                ((ACSP) response.getData().get("acsp")).getAppointements().getOfficers().getNationalityOther());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getBusinessName());
        Assertions.assertEquals(COUNTRY_OF_RESIDENCE.toUpperCase(),
                ((ACSP) response.getData().get("acsp")).getAppointements().getOfficers().getUsualResidence());
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
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getBusinessName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getPersonName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).
                                getAppointements().getOfficers().getPersonName().getFirstName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).
                                getAppointements().getOfficers().getPersonName().getLastName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).
                                getAppointements().getOfficers().getPersonName().getMiddleName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).
                                getAppointements().getOfficers().getBirthDate());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).
                                getAppointements().getOfficers().getUsualResidence());
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


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.SOLE_TRADER);
        var response = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertEquals("businessName".toUpperCase(), ((ACSP) response.getData().get("acsp")).getBusinessName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertEquals(FIRST_NAME.toUpperCase(),
                ((ACSP) response.getData().get("acsp")).getAppointements().getOfficers().getPersonName().getFirstName());
        Assertions.assertEquals("1984-10-31",
                ((ACSP) response.getData().get("acsp")).getAppointements().getOfficers().getBirthDate());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).
                                    getAppointements().getOfficers().getNationalityOther());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).
                                    getAppointements().getOfficers().getUsualResidence());
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


        AMLSupervisoryBodiesDto amlSupervisoryBodies1 = new AMLSupervisoryBodiesDto();
        amlSupervisoryBodies1.setAmlSupervisoryBody("hmrc");
        amlSupervisoryBodies1.setMembershipId("12345678");
        AMLSupervisoryBodiesDto[] amlSupervisoryBodies = new AMLSupervisoryBodiesDto[]{amlSupervisoryBodies1};
        acspDataDto.setAmlSupervisoryBodies(amlSupervisoryBodies);
        when(acspService.getAcsp(any(), any())).thenReturn(Optional.of(acspDataDto));
        when(transactionService.getTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID)).thenReturn(transaction);
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.CORPORATE_BODY);
        var response2 = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(((ACSP) response2.getData().get("acsp")).getOfficeAddress());

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
        Assertions.assertNotNull(((ACSP) response2.getData().get("acsp")).getOfficeAddress());

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
        acspDataDto.setTypeOfBusiness(TypeOfBusiness.UNINCORPORATED_ENTITY);
        var response2 = filingsService.generateAcspApplicationFiling(ACSP_ID, TRANSACTION_ID, PASS_THROUGH_HEADER);
        Assertions.assertNotNull(((ACSP) response2.getData().get("acsp")).getOfficeAddress());

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
        Assertions.assertNotNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertEquals("COUNTRY", ((ACSP) response.getData().get("acsp")).getOfficeAddress().getCountry());

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
        Assertions.assertNotNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertEquals(true, ((ACSP) response.getData().get("acsp")).isServiceAddressROA());
        Assertions.assertEquals(MIDDLE_NAME.toUpperCase(), ((ACSP) response.getData().get("acsp")).getPersonName().getMiddleName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getPersonName().getFirstName());
        Assertions.assertNull(((ACSP) response.getData().get("acsp")).getPersonName().getLastName());

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
        Assertions.assertNotNull(((ACSP) response.getData().get("acsp")).getOfficeAddress());
        Assertions.assertEquals(false, ((ACSP) response.getData().get("acsp")).isServiceAddressROA());

    }








}
