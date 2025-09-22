package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.dto.AddressDto;
import uk.gov.companieshouse.acsp.models.enums.AcspType;
import uk.gov.companieshouse.acsp.models.enums.BusinessSector;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.filing.Aml;
import uk.gov.companieshouse.acsp.models.filing.ServiceAddress;
import uk.gov.companieshouse.acsp.models.filing.STPersonalInformation;
import uk.gov.companieshouse.acsp.models.filing.Address;
import uk.gov.companieshouse.acsp.models.filing.AmlMembership;
import uk.gov.companieshouse.acsp.models.filing.PersonName;
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
import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_ACSP;
import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_CLOSE_ACSP;
import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_UPDATE_ACSP;

@Service
public class FilingsService {


  private static final Logger LOGGER = LoggerFactory.getLogger(APP_NAMESPACE);

  @Value("${ACSP01_COST}")
  private String costAmount;

  @Value("${ACSP_APPLICATION_FILING_DESCRIPTION_IDENTIFIER}")
  private String filingDescriptionIdentifier;

  @Value("${ACSP_APPLICATION_FILING_DESCRIPTION}")
  private String filingDescription;

  @Value("${ACSP_UPDATE_FILING_DESCRIPTION}")
  private String updateFilingDescription;

  @Value("${ACSP_CLOSE_FILING_DESCRIPTION}")
  private String closeFilingDescription;

  private final TransactionService transactionService;

  private final ApiClientService apiClientService;

  private final AcspService acspService;

  private static final String REGISTERED_OFFICE_ADDRESS = "registered_office_address";

  private static final String SERVICE_ADDRESS = "service_address";

  private static final String EMAIL = "email";

  private static final String BUSINESS_SECTOR = "business_sector";

  private static final String ST_PERSONAL_INFORMATION = "st_personal_information";

  private static final String AML = "aml";

  private static final String BUSINESS_NAME = "business_name";

  private static final String ACSP_TYPE = "acsp_type";

  private static final String COMPANY_NUMBER = "company_number";

  private static final String PAYMENT_REFERENCE = "payment_reference";

  private static final String PAYMENT_METHOD = "payment_method";

  private static final String DATE_COMPONENT = "{date}";

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
      AcspType acspType =  acspDataDto.getAcspType();
      filing.setData(buildData(acspDataDto, transaction, passThroughTokenHeader, acspType));
      setDescriptionFields(filing, transaction, acspType);
      buildFilingStatus(filing, acspType);
    }
  }

  private void buildFilingStatus(FilingApi filing, AcspType acspType) {
    if (AcspType.REGISTER_ACSP.equals(acspType)) {
      filing.setKind(FILING_KIND_ACSP);
      filing.setCost(costAmount);
    } else if (AcspType.UPDATE_ACSP.equals(acspType)) {
      filing.setKind(FILING_KIND_UPDATE_ACSP);
    } else if (AcspType.CLOSE_ACSP.equals(acspType)) {
      filing.setKind(FILING_KIND_CLOSE_ACSP);
    }
  }

  private Map<String, Object> buildData(AcspDataDto acspDataDto, Transaction transaction,
                                        String passThroughTokenHeader, AcspType acspType) throws ServiceException {
    Map<String, Object> data = new HashMap<>();
    buildAcspData(data, acspDataDto, transaction, passThroughTokenHeader, acspType);
    return data;
  }

  private void buildAcspData(Map<String, Object>data, AcspDataDto acspDataDto, Transaction transaction,
                             String passThroughTokenHeader, AcspType acspType) throws ServiceException {

    // Common data for Register and Update ACSP
    // Extracted methods to reduce cognitive complexity

    addCommonRegisterOrUpdateData(data, acspDataDto);

    if (AcspType.REGISTER_ACSP.equals(acspType)) {
      addRegisterAcspData(data, acspDataDto, transaction, passThroughTokenHeader);
    } else if (AcspType.UPDATE_ACSP.equals(acspType)) {
      addUpdateAcspData(data, acspDataDto);
    }

    if (AcspType.UPDATE_ACSP.equals(acspType) || AcspType.CLOSE_ACSP.equals(acspType)) {
      data.put(COMPANY_NUMBER, Optional.ofNullable(acspDataDto.getAcspId())
              .map(String::toUpperCase).orElse(null));
    }
  }

  private void addCommonRegisterOrUpdateData(Map<String, Object> data, AcspDataDto acspDataDto) {
    if (acspDataDto.getApplicantDetails() != null) {
      data.put(EMAIL, Optional.ofNullable(acspDataDto.getApplicantDetails().getCorrespondenceEmail())
              .map(String::toUpperCase).orElse(null));
    }
    if (TypeOfBusiness.SOLE_TRADER.equals(acspDataDto.getTypeOfBusiness()) ||
            AcspType.UPDATE_ACSP.equals(acspDataDto.getAcspType())) {
      data.put(ST_PERSONAL_INFORMATION, buildStPersonalInformation(acspDataDto));
    }
  }

  private void addRegisterAcspData(Map<String, Object> data, AcspDataDto acspDataDto, Transaction transaction, String passThroughTokenHeader) throws ServiceException {
    TypeOfBusiness type = acspDataDto.getTypeOfBusiness();
    if (TypeOfBusiness.LP.equals(type) ||
            TypeOfBusiness.PARTNERSHIP.equals(type) ||
            TypeOfBusiness.UNINCORPORATED.equals(type)) {
      data.put(REGISTERED_OFFICE_ADDRESS, buildRegisteredOfficeAddress(acspDataDto));
      data.put(SERVICE_ADDRESS, buildServiceAddress(acspDataDto));
    } else if (TypeOfBusiness.SOLE_TRADER.equals(type)) {
      data.put(REGISTERED_OFFICE_ADDRESS, buildCorrespondenceAddress(acspDataDto));
    } else {
      data.put(SERVICE_ADDRESS, buildServiceAddress(acspDataDto));
    }

    if (transaction.getStatus() != null && TransactionStatus.CLOSED.equals(transaction.getStatus())) {
      setPaymentData(data, transaction, passThroughTokenHeader);
    }

    data.put(ACSP_TYPE, Optional.ofNullable(type)
            .map(TypeOfBusiness::name).orElse(null));

    if (acspDataDto.getCompanyDetails() != null || acspDataDto.getBusinessName() != null) {
      buildCompanyDetails(acspDataDto, data);
    }

    BusinessSector sector = acspDataDto.getWorkSector();
    if (BusinessSector.PNTS.equals(sector)) {
      data.put(BUSINESS_SECTOR, null);
    } else {
      data.put(BUSINESS_SECTOR, Optional.ofNullable(sector)
              .map(BusinessSector::name).orElse(null));
    }

    if (acspDataDto.getAmlSupervisoryBodies() != null) {
      data.put(AML, buildAml(acspDataDto));
    }
  }

  private void addUpdateAcspData(Map<String, Object> data, AcspDataDto acspDataDto) {
    data.put(BUSINESS_NAME, Optional.ofNullable(acspDataDto.getBusinessName())
            .map(String::toUpperCase).orElse(null));
    data.put(AML, buildAml(acspDataDto));

    if (acspDataDto.getRegisteredOfficeAddress() != null) {
      data.put(REGISTERED_OFFICE_ADDRESS, buildRegisteredOfficeAddress(acspDataDto));
    }

    if (acspDataDto.getApplicantDetails() != null &&
            acspDataDto.getApplicantDetails().getCorrespondenceAddress() != null) {
      data.put(SERVICE_ADDRESS, buildServiceAddress(acspDataDto));
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
    if (buildPersonName(acspDataDto) != null) {
      aml.setPersonName(buildPersonName(acspDataDto));
    }
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
    if (acspDataDto.getApplicantDetails() != null &&
        (acspDataDto.getApplicantDetails().getFirstName() != null ||
         acspDataDto.getApplicantDetails().getLastName() != null ||
         acspDataDto.getApplicantDetails().getMiddleName() != null)) {
      personName.setFirstName(Optional.ofNullable(acspDataDto.getApplicantDetails().getFirstName())
              .map(String::toUpperCase).orElse(null));
      personName.setLastName(Optional.ofNullable(acspDataDto.getApplicantDetails().getLastName())
              .map(String::toUpperCase).orElse(null));
      personName.setMiddleName(Optional.ofNullable(acspDataDto.getApplicantDetails().getMiddleName())
              .map(String::toUpperCase).orElse(null));
      return personName;
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
      if (acspDataDto.getTypeOfBusiness() == TypeOfBusiness.LC ||
          acspDataDto.getTypeOfBusiness() == TypeOfBusiness.LLP ||
          acspDataDto.getTypeOfBusiness() == TypeOfBusiness.CORPORATE_BODY) {
        if (acspDataDto.getCompanyDetails() != null) {
          data.put(COMPANY_NUMBER, Optional.ofNullable(acspDataDto.getCompanyDetails().getCompanyNumber())
                  .map(String::toUpperCase).orElse(null));
          data.put("company_name", Optional.ofNullable(acspDataDto.getCompanyDetails().getCompanyName())
                  .map(String::toUpperCase).orElse(null));
        }
      } else {
        data.put(BUSINESS_NAME, Optional.ofNullable(acspDataDto.getBusinessName())
                          .map(String::toUpperCase).orElse(null));
      }
    }
  }

  private ServiceAddress buildServiceAddress(AcspDataDto acspDataDto) {
    var serviceAddress = new ServiceAddress();
    if(acspDataDto != null && acspDataDto.getApplicantDetails() != null) {
      if (acspDataDto.getApplicantDetails().getCorrespondenceAddressIsSameAsRegisteredOfficeAddress()) {
        serviceAddress.setServiceAddressROA(true);
      } else {
        serviceAddress.setCorrespondenceAddress(buildCorrespondenceAddress(acspDataDto));
        serviceAddress.setServiceAddressROA(false);
      }
    }
    return serviceAddress;
  }

  private Address buildCorrespondenceAddress(AcspDataDto acspDataDto) {
    return buildAddress(acspDataDto.getApplicantDetails().getCorrespondenceAddress());
  }

  private Address buildRegisteredOfficeAddress(AcspDataDto acspDataDto) {
    return buildAddress(acspDataDto.getRegisteredOfficeAddress());
  }

  private Address buildAddress(AddressDto addressDto) {
    var address = new Address();
    if (addressDto != null) {
      address.setAddressLine1(Optional.ofNullable(addressDto.getAddressLine1()).map(String::toUpperCase).orElse(null));
      address.setAddressLine2(Optional.ofNullable(addressDto.getAddressLine2()).map(String::toUpperCase).orElse(null));
      address.setPostalCode(Optional.ofNullable(addressDto.getPostalCode()).map(String::toUpperCase).orElse(null));
      address.setCountry(Optional.ofNullable(addressDto.getCountry()).map(String::toUpperCase).orElse(null));
      address.setPremises(Optional.ofNullable(addressDto.getPremises()).map(String::toUpperCase).orElse(null));
      address.setRegion(Optional.ofNullable(addressDto.getRegion()).map(String::toUpperCase).orElse(null));
      address.setLocality(Optional.ofNullable(addressDto.getLocality()).map(String::toUpperCase).orElse(null));
    }
    return address;
  }

  private void setDescriptionFields(FilingApi filing, Transaction transaction, AcspType acspType) {
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
    if (AcspType.REGISTER_ACSP.equals(acspType) && filingDescription != null) {
      filing.setDescription(filingDescription.replace(DATE_COMPONENT, formattedDate).toUpperCase());
    } else if (AcspType.UPDATE_ACSP.equals(acspType) && updateFilingDescription != null) {
      filing.setDescription(updateFilingDescription.replace(DATE_COMPONENT, formattedDate).toUpperCase());
    } else if (AcspType.CLOSE_ACSP.equals(acspType) && closeFilingDescription != null) {
      filing.setDescription(closeFilingDescription.replace(DATE_COMPONENT, formattedDate).toUpperCase());
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

    data.put(PAYMENT_REFERENCE, paymentReference.toUpperCase());
    data.put(PAYMENT_METHOD, payment.getPaymentMethod().toUpperCase());
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
