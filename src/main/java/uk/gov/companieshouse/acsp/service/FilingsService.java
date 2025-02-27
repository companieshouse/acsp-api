package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.filing.Presenter;
import uk.gov.companieshouse.acsp.models.filing.Aml;
import uk.gov.companieshouse.acsp.models.filing.ServiceAddress;
import uk.gov.companieshouse.acsp.models.filing.STPersonalInformation;
import uk.gov.companieshouse.acsp.models.filing.Address;
import uk.gov.companieshouse.acsp.models.filing.AmlMembership;
import uk.gov.companieshouse.acsp.models.filing.PersonName;
import uk.gov.companieshouse.acsp.models.filing.Submission;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Map;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import static uk.gov.companieshouse.acsp.AcspApplication.APP_NAMESPACE;
import static uk.gov.companieshouse.acsp.models.enums.AcspType.REGISTER_ACSP;
import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_ACSP;

@Service
public class FilingsService {


  private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

  @Value("${ACSP01_COST}")
  private String costAmount;

  @Value("${ACSP_APPLICATION_FILING_DESCRIPTION_IDENTIFIER}")
  private String filingDescriptionIdentifier;

  @Value("${ACSP_APPLICATION_FILING_DESCRIPTION}")
  private String filingDescription;

  private TransactionService transactionService;

  private final ApiClientService apiClientService;

  private AcspService acspService;

  private static final String PRESENTER = "presenter";

  private static final String SUBMISSION = "submission";

  private static final String REGISTERED_OFFICE_ADDRESS = "registered_office_address";

  private static final String SERVICE_ADDRESS = "service_address";

  private static final String EMAIL = "email";

  private static final String BUSINESS_SECTOR = "business_sector";

  private static final String ST_PERSONAL_INFORMATION = "st_personal_information";

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  @Autowired
  public FilingsService(TransactionService transactionService, AcspService acspService,
                        ApiClientService apiClientService) {
    this.transactionService = transactionService;
    this.acspService = acspService;
    this.apiClientService = apiClientService;
  }

  public FilingApi generateAcspApplicationFiling(
          String acspApplicationId,
          String transactionId,
          String passThroughTokenHeader)
          throws ServiceException, SubmissionNotLinkedToTransactionException {
    LOGGER.debug("starting generateAcspApplicationFiling--------------");
    var filing = new FilingApi();
    setFilingApiData(filing, acspApplicationId, transactionId, passThroughTokenHeader);
    return filing;
  }

  private void setFilingApiData(FilingApi filing, String acspApplicationId, String transactionId,
                                String passThroughTokenHeader) throws ServiceException, SubmissionNotLinkedToTransactionException {
    var transaction = transactionService.getTransaction(passThroughTokenHeader, transactionId);
    var acspDataDto = acspService.getAcsp(acspApplicationId, transaction).orElse(null);
    if(acspDataDto != null) {
      boolean isRegistration =  REGISTER_ACSP.equals(acspDataDto.getAcspType());
      filing.setData(buildData(acspDataDto, transactionId, transaction, passThroughTokenHeader, isRegistration));
      setDescriptionFields(filing, transaction);
      buildFilingStatus(filing, isRegistration);
    }
  }

  private void buildFilingStatus(FilingApi filing, boolean isRegistration) {
    filing.setKind(FILING_KIND_ACSP);
    if (isRegistration) {
      filing.setCost(costAmount);
    }
  }

  private void buildPresenter(Map<String, Object> data, AcspDataDto acspDataDto) {
    var presenter = new Presenter();

    if (acspDataDto.getApplicantDetails() != null) {
      presenter.setFirstName(Optional.ofNullable(acspDataDto.getApplicantDetails().getFirstName())
              .map(String::toUpperCase).orElse(null));
      presenter.setLastName(Optional.ofNullable(acspDataDto.getApplicantDetails().getLastName())
              .map(String::toUpperCase).orElse(null));
    }
    presenter.setUserId(Optional.ofNullable(acspDataDto.getAcspDataSubmission().getLastModifiedByUserId())
            .map(String::toUpperCase).orElse(null));

    // presenter.setLanguage(); // add language in acspDataModel
    data.put(PRESENTER, presenter);
  }

  private void buildSubmission(Map<String, Object>data, AcspDataDto acspDataDto, String transactionId) {
    var submission = new Submission();
    submission.setReceivedAt(acspDataDto.getAcspDataSubmission().getUpdatedAt());
    submission.setTransactionId(transactionId.toUpperCase());
    data.put(SUBMISSION, submission);
  }

  private Map<String, Object> buildData(AcspDataDto acspDataDto, String transactionId, Transaction transaction,
                                            String passThroughTokenHeader, boolean isRegistration) throws ServiceException {
    Map<String, Object> data = new HashMap<>();
    if (!isRegistration) {
      buildUpdateAcspData(data, acspDataDto);
    } else {
      buildAcspData(data, acspDataDto, transaction,passThroughTokenHeader);
    }
    buildPresenter(data, acspDataDto);
    buildSubmission(data, acspDataDto, transactionId);
    //item.setSubmissionLanguage(acspDataDto.getLanguage()); //add language in ascpDataModel
    return data;
  }

  private void buildAcspData(Map<String, Object>data, AcspDataDto acspDataDto, Transaction transaction,
                             String passThroughTokenHeader) throws ServiceException {
    if (acspDataDto.getApplicantDetails() != null) {
      data.put(EMAIL, Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceEmail()).map((String::toUpperCase)).orElse(null));
    }
    if(TypeOfBusiness.LP.equals(acspDataDto.getTypeOfBusiness()) ||
            TypeOfBusiness.PARTNERSHIP.equals(acspDataDto.getTypeOfBusiness())||
            TypeOfBusiness.UNINCORPORATED.equals(acspDataDto.getTypeOfBusiness())) {
      data.put(REGISTERED_OFFICE_ADDRESS, buildRegisteredOfficeAddress(acspDataDto));
      data.put(SERVICE_ADDRESS,buildServiceAddress(acspDataDto));
    } else if (TypeOfBusiness.SOLE_TRADER.equals(acspDataDto.getTypeOfBusiness())) {
      data.put(REGISTERED_OFFICE_ADDRESS, buildCorrespondenAddress(acspDataDto));
    } else {
      data.put(SERVICE_ADDRESS,buildServiceAddress(acspDataDto));
    }

    if(transaction.getStatus() != null &&
            TransactionStatus.CLOSED.equals(transaction.getStatus())) {
      setPaymentData(data, transaction, passThroughTokenHeader);
    }

    data.put("acsp_type", Optional.ofNullable(acspDataDto.getTypeOfBusiness()).map(TypeOfBusiness::name).orElse(null));

    if(acspDataDto.getCompanyDetails() != null || acspDataDto.getBusinessName() != null) {
      buildCompanyDetails(acspDataDto, data);
    }

    if(BusinessSector.PNTS.equals(acspDataDto.getWorkSector())) {
      data.put(BUSINESS_SECTOR, null);
    } else {
      data.put(BUSINESS_SECTOR, Optional.ofNullable(acspDataDto.getWorkSector()).map((BusinessSector::name)).orElse(null));
    }

    if(acspDataDto.getAmlSupervisoryBodies() != null) {
      data.put("aml", buildAml(acspDataDto));
    }

    if(acspDataDto.getTypeOfBusiness() != null &&
            TypeOfBusiness.SOLE_TRADER.equals(acspDataDto.getTypeOfBusiness())) {
      data.put(ST_PERSONAL_INFORMATION, buildStPersonalInformation(acspDataDto));
    }
    //item.setSubmissionLanguage(acspDataDto.getLanguage()); //add language in ascpDataModel
  }

  private void buildUpdateAcspData(Map<String, Object> data, AcspDataDto acspDataDto) {

    data.put("incorporation_number", Optional.ofNullable(acspDataDto.getAcspId()).map(String::toUpperCase).orElse(null));

    data.put(EMAIL, Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceEmail()).map((String::toUpperCase)).orElse(null));

    data.put("proposed_corporate_body_name", Optional.ofNullable(acspDataDto.getBusinessName()).map(String::toUpperCase).orElse(null));

    data.put("acsp_details", buildAml(acspDataDto));

    if(acspDataDto.getRegisteredOfficeAddress() != null) {
      data.put(REGISTERED_OFFICE_ADDRESS, buildRegisteredOfficeAddress(acspDataDto));
    }

    if(acspDataDto.getApplicantDetails().getCorrespondenceAddress() != null) {
      data.put(SERVICE_ADDRESS, buildServiceAddress(acspDataDto));
    }

    if(acspDataDto.getTypeOfBusiness() != null &&
            TypeOfBusiness.SOLE_TRADER.equals(acspDataDto.getTypeOfBusiness())) {
      data.put(ST_PERSONAL_INFORMATION, buildStPersonalInformation(acspDataDto));
    }
  }

  private Aml buildAml(AcspDataDto acspDataDto) {
    var aml = new Aml();
    if (acspDataDto.getRemovedAmlSupervisoryBodies() != null) {
      aml.setPreviousAmlMemberships(buildAmlMemberships(acspDataDto, true));
    }
    if (acspDataDto.getAmlSupervisoryBodies() != null) {
      aml.setAmlMemberships(buildAmlMemberships(acspDataDto, false));
    }
    aml.setPersonName(buildPersonName(acspDataDto));
    return aml;
  }

  private AmlMembership[] buildAmlMemberships(AcspDataDto acspDataDto, boolean isRemoved) {
    var amlMemberships = new ArrayList<AmlMembership>();
    Arrays.stream(isRemoved ? acspDataDto.getRemovedAmlSupervisoryBodies() : acspDataDto.getAmlSupervisoryBodies()).forEach(amlSupervisoryBodiesDto -> {
                var membership = new AmlMembership();
                membership.setRegistrationNumber(amlSupervisoryBodiesDto.getMembershipId().toUpperCase());
                membership.setSupervisoryBody(amlSupervisoryBodiesDto.getAmlSupervisoryBody().name());
                amlMemberships.add(membership);
              });
    var amlMembershipsArray = new AmlMembership[amlMemberships.size()];
    for(var counter = 0; counter < amlMemberships.size(); counter++) {
      amlMembershipsArray[counter] = amlMemberships.get(counter);
    }
    return amlMembershipsArray;
  }

  private PersonName buildPersonName(AcspDataDto acspDataDto) {
    var personName = new PersonName();
    if (acspDataDto.getApplicantDetails() != null) {
    if (acspDataDto.getApplicantDetails().getFirstName() != null || acspDataDto.getApplicantDetails().getLastName() != null || acspDataDto.getApplicantDetails().getMiddleName() != null) {
      personName.setFirstName(Optional.ofNullable(acspDataDto.getApplicantDetails().getFirstName())
              .map(String::toUpperCase).orElse(null));
      personName.setLastName(Optional.ofNullable(acspDataDto.getApplicantDetails().getLastName())
              .map(String::toUpperCase).orElse(null));
      personName.setMiddleName(Optional.ofNullable(acspDataDto.getApplicantDetails().getMiddleName())
              .map(String::toUpperCase).orElse(null));
      return personName;
    }
    }
    return null;
  }
  private STPersonalInformation buildStPersonalInformation(AcspDataDto acspDataDto) {
    var stPersonalInformation = new STPersonalInformation();
    stPersonalInformation.setPersonName(buildPersonName(acspDataDto));
    if (acspDataDto.getApplicantDetails() != null) {
      if (acspDataDto.getApplicantDetails().getDateOfBirth() != null) {
        stPersonalInformation.setBirthDate(acspDataDto.getApplicantDetails().getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      }

      if (acspDataDto.getApplicantDetails().getNationality() != null) {
        var nationalities = getNationalities(acspDataDto);
        stPersonalInformation.setNationalityOther(String.join(",", nationalities).toUpperCase());
      }

      stPersonalInformation.setUsualResidence(Optional.ofNullable(acspDataDto.getApplicantDetails().getCountryOfResidence())
              .map(String::toUpperCase).orElse(null));
    }
    return stPersonalInformation;
  }

  private ArrayList<String> getNationalities(AcspDataDto acspDataDto) {
    var nationalities = new ArrayList<String>();
    if (acspDataDto.getApplicantDetails() != null) {
      if (acspDataDto.getApplicantDetails().getNationality().getFirstNationality() != null &&
              !acspDataDto.getApplicantDetails().getNationality().getFirstNationality().isEmpty()) {
        nationalities.add(acspDataDto.getApplicantDetails().getNationality().getFirstNationality());
      }
      if (acspDataDto.getApplicantDetails().getNationality().getSecondNationality() != null &&
              !acspDataDto.getApplicantDetails().getNationality().getSecondNationality().isEmpty()) {
        nationalities.add(acspDataDto.getApplicantDetails().getNationality().getSecondNationality());
      }
      if (acspDataDto.getApplicantDetails().getNationality().getThirdNationality() != null &&
              !acspDataDto.getApplicantDetails().getNationality().getThirdNationality().isEmpty()) {
        nationalities.add(acspDataDto.getApplicantDetails().getNationality().getThirdNationality());
      }
    }
    return nationalities;
  }

  private void buildCompanyDetails(AcspDataDto acspDataDto, Map<String, Object>data) {
    if (acspDataDto.getTypeOfBusiness() != null) {
      switch (acspDataDto.getTypeOfBusiness()) {
        case LC, LLP, CORPORATE_BODY:
          if (acspDataDto.getCompanyDetails() != null) {
            data.put("company_number", Optional.ofNullable(acspDataDto.getCompanyDetails().getCompanyNumber())
                    .map(String::toUpperCase).orElse(null));
            data.put("company_name", Optional.ofNullable(acspDataDto.getCompanyDetails().getCompanyName())
                    .map(String::toUpperCase).orElse(null));
          }
          break;
        default:
          data.put("business_name", Optional.ofNullable(acspDataDto.getBusinessName())
                            .map(String::toUpperCase).orElse(null));
          break;
      }
    }
  }

  private ServiceAddress buildServiceAddress(AcspDataDto acspDataDto) {
    var serviceAddress = new ServiceAddress();
    if(acspDataDto != null && acspDataDto.getApplicantDetails() != null) {
      if (acspDataDto.getApplicantDetails().getCorrespondenceAddressIsSameAsRegisteredOfficeAddress()) {
        serviceAddress.setServiceAddressROA(true);
      } else {
        serviceAddress.setCorrespondenceAddress(buildCorrespondenAddress(acspDataDto));
        serviceAddress.setServiceAddressROA(false);
      }
    }
    return serviceAddress;
  }

  private Address buildCorrespondenAddress(AcspDataDto acspDataDto) {
    var correspondenceAddress = new Address();
    if (acspDataDto.getApplicantDetails() != null) {
      if (acspDataDto.getApplicantDetails().getCorrespondenceAddress() != null) {
        correspondenceAddress.setAddressLine1(
                Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceAddress().getAddressLine1())
                        .map(String::toUpperCase).orElse(null));
        correspondenceAddress.setAddressLine2(
                Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceAddress().getAddressLine2())
                        .map(String::toUpperCase).orElse(null));
        correspondenceAddress.setPostalCode(
                Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceAddress().getPostalCode())
                        .map(String::toUpperCase).orElse(null));
        correspondenceAddress.setCountry(
                Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceAddress().getCountry())
                        .map(String::toUpperCase).orElse(null));
        correspondenceAddress.setPremises(
                Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceAddress().getPremises())
                        .map(String::toUpperCase).orElse(null));
        correspondenceAddress.setRegion(
                Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceAddress().getRegion())
                        .map(String::toUpperCase).orElse(null));
        correspondenceAddress.setLocality(
                Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceAddress().getLocality())
                        .map(String::toUpperCase).orElse(null));
      }
    }
    return correspondenceAddress;
  }

  private Address buildRegisteredOfficeAddress(AcspDataDto acspDataDto) {
    var registeredOfficeAddress = new Address();
    if(acspDataDto.getRegisteredOfficeAddress() != null) {
        registeredOfficeAddress.setAddressLine1(
              Optional.ofNullable(acspDataDto.getRegisteredOfficeAddress().getAddressLine1())
                      .map(String::toUpperCase).orElse(null));
        registeredOfficeAddress.setAddressLine2(
              Optional.ofNullable(acspDataDto.getRegisteredOfficeAddress().getAddressLine2())
                      .map(String::toUpperCase).orElse(null));
        registeredOfficeAddress.setPostalCode(
              Optional.ofNullable(acspDataDto.getRegisteredOfficeAddress().getPostalCode())
                      .map(String::toUpperCase).orElse(null));
      registeredOfficeAddress.setCountry(
              Optional.ofNullable(acspDataDto.getRegisteredOfficeAddress().getCountry())
                      .map(String::toUpperCase).orElse(null));
        registeredOfficeAddress.setPremises(
              Optional.ofNullable(acspDataDto.getRegisteredOfficeAddress().getPremises())
                      .map(String::toUpperCase).orElse(null));
        registeredOfficeAddress.setRegion(
              Optional.ofNullable(acspDataDto.getRegisteredOfficeAddress().getRegion())
                      .map(String::toUpperCase).orElse(null));
      registeredOfficeAddress.setLocality(
              Optional.ofNullable(acspDataDto.getRegisteredOfficeAddress().getLocality())
                      .map(String::toUpperCase).orElse(null));
    }
    return registeredOfficeAddress;
  }

  private void setDescriptionFields(FilingApi filing, Transaction transaction) {
    var formattedDate = "";
    if(transaction.getClosedAt() != null) {
      var datofCreation = LocalDate.parse(
              transaction.getClosedAt().substring(0,
                      transaction.getClosedAt().indexOf("T")));
      formattedDate = datofCreation.format(formatter);
    }
    if(filingDescriptionIdentifier != null) {
      filing.setDescriptionIdentifier(filingDescriptionIdentifier.toUpperCase());
    }
    if(filingDescription != null) {
      filing.setDescription(filingDescription.replace("{date}", formattedDate).toUpperCase());
    }
    Map<String, String> values = new HashMap<>();
    filing.setDescriptionValues(values);
  }

  private void setPaymentData(
          Map<String, Object>data,
          Transaction transaction,
          String passthroughTokenHeader)
          throws ServiceException {
    var paymentLink = transaction.getLinks().getPayment();
    var paymentReference = getPaymentReferenceFromTransaction(paymentLink, passthroughTokenHeader);
    var payment = getPayment(paymentReference, passthroughTokenHeader);

    data.put("payment_reference", paymentReference.toUpperCase());
    data.put("payment_method", payment.getPaymentMethod().toUpperCase());
  }

  private PaymentApi getPayment(String paymentReference, String passthroughTokenHeader)
          throws ServiceException {
    try {
      return apiClientService
              .getApiClient(passthroughTokenHeader)
              .payment()
              .get("/payments/" + paymentReference)
              .execute()
              .getData();
    } catch (URIValidationException | IOException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

  private String getPaymentReferenceFromTransaction(String uri, String passthroughTokenHeader)
          throws ServiceException {
    try {
      var transactionPaymentInfo =
              apiClientService
                      .getApiClient(passthroughTokenHeader)
                      .transactions()
                      .getPayment(uri)
                      .execute();

      return transactionPaymentInfo.getData().getPaymentReference();
    } catch (URIValidationException | IOException e) {
      throw new ServiceException(e.getMessage(), e);
    }
  }

}
