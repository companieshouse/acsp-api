package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.filing.ACSP;
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
      filing.setData(buildData(acspDataDto, transactionId, transaction, passThroughTokenHeader));
      setDescriptionFields(filing, transaction);
      buildFilingStatus(filing);
    }
  }

  private void buildFilingStatus(FilingApi filing) {
    filing.setKind(FILING_KIND_ACSP.toUpperCase());
    filing.setCost(costAmount);
  }

  private void buildPresenter(Map<String, Object>data, AcspDataDto acspDataDto) {
    var presenter = new Presenter();

    presenter.setFirstName(Optional.ofNullable(acspDataDto.getFirstName())
            .map(String::toUpperCase).orElse(null));
    presenter.setLastName(Optional.ofNullable(acspDataDto.getLastName())
            .map(String::toUpperCase).orElse(null));
    presenter.setUserId(Optional.ofNullable(acspDataDto.getId())
            .map(String::toUpperCase).orElse(null));
    //presenter.setLanguage(); //add language in ascpDataModel
    data.put(PRESENTER, presenter);
  }


  private void buildSubmission(Map<String, Object>data, AcspDataDto acspDataDto, String transactionId) {
    var submission = new Submission();
    submission.setReceivedAt(acspDataDto.getAcspDataSubmission().getUpdatedAt());
    submission.setTransactionId(transactionId.toUpperCase());
    data.put(SUBMISSION, submission);
  }

  private Map<String, Object> buildData(AcspDataDto acspDataDto, String transactionId, Transaction transaction,
                                            String passThroughTokenHeader) throws ServiceException {
    Map<String, Object> data = new HashMap<>();
    data.put("acsp", buildAcspData(acspDataDto, transaction,passThroughTokenHeader));
    buildPresenter(data, acspDataDto);
    buildSubmission(data, acspDataDto, transactionId);
    //item.setSubmissionLanguage(acspDataDto.getLanguage()); //add language in ascpDataModel
    return data;
  }

  private ACSP buildAcspData(AcspDataDto acspDataDto, Transaction transaction,
                             String passThroughTokenHeader) throws ServiceException {
    var acsp = new ACSP();
    acsp.setEmail(Optional.ofNullable(acspDataDto.getEmail()).map((String::toUpperCase)).orElse(null));

    if(TypeOfBusiness.corporate_body.equals(acspDataDto.getTypeOfBusiness()) ||
            TypeOfBusiness.partnership.equals(acspDataDto.getTypeOfBusiness())||
            TypeOfBusiness.unincorporated.equals(acspDataDto.getTypeOfBusiness())) {
      acsp.setOfficeAddress(buildBusinessAddress(acspDataDto));
    }
    if(acspDataDto.getBusinessAddress() != null &&
            acspDataDto.getBusinessAddress().equals(acspDataDto.getCorrespondenceAddress())) {
      acsp.setServiceAddress(buildServiceAddress(null, true));
    } else {
      acsp.setServiceAddress(buildServiceAddress(acspDataDto, false));
    }
    if(transaction.getStatus() != null &&
            TransactionStatus.CLOSED.equals(transaction.getStatus())) {
      setPaymentData(acsp, transaction, passThroughTokenHeader);
    }

    acsp.setAcspType(Optional.ofNullable(acspDataDto.getTypeOfBusiness()).map(TypeOfBusiness::name).orElse(null));

    if(acspDataDto.getCompanyDetails() != null || acspDataDto.getBusinessName() != null) {
      buildCompanyDetails(acspDataDto, acsp);
    }
    acsp.setBusinessSector(Optional.ofNullable(acspDataDto.getWorkSector()).map((String::toUpperCase)).orElse(null));

    if(acspDataDto.getAmlSupervisoryBodies() != null) {
      acsp.setAml(buildAml(acspDataDto));
    }

    if(acspDataDto.getTypeOfBusiness() != null &&
            TypeOfBusiness.sole_trader.equals(acspDataDto.getTypeOfBusiness())) {
      buildStPersonalInformation(acspDataDto, acsp);
    }
    //item.setSubmissionLanguage(acspDataDto.getLanguage()); //add language in ascpDataModel
    return acsp;
  }

  private Aml buildAml(AcspDataDto acspDataDto) {
    var aml = new Aml();
    aml.setAmlMemberships(buildAmlMemberships(acspDataDto));
    aml.setPersonName(buildPersonName(acspDataDto));
    return aml;
  }

  private AmlMembership[] buildAmlMemberships(AcspDataDto acspDataDto) {
    var amlMemberships = new ArrayList<AmlMembership>();
    Arrays.stream(acspDataDto.getAmlSupervisoryBodies()).forEach(amlSupervisoryBodiesDto -> {
      var membership = new AmlMembership();
      membership.setRegistrationNumber(amlSupervisoryBodiesDto.getMembershipId().toUpperCase());
      membership.setSupervisoryBody(amlSupervisoryBodiesDto.getAmlSupervisoryBody().toUpperCase());
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
    if (acspDataDto.getFirstName() != null || acspDataDto.getLastName() != null || acspDataDto.getMiddleName() != null) {
      personName.setFirstName(Optional.ofNullable(acspDataDto.getFirstName())
              .map(String::toUpperCase).orElse(null));
      personName.setLastName(Optional.ofNullable(acspDataDto.getLastName())
              .map(String::toUpperCase).orElse(null));
      personName.setMiddleName(Optional.ofNullable(acspDataDto.getMiddleName())
              .map(String::toUpperCase).orElse(null));
      return personName;
    }
    return null;
  }
  private void buildStPersonalInformation(AcspDataDto acspDataDto, ACSP acsp) {
    var stPersonalInformation = new STPersonalInformation();
    stPersonalInformation.setPersonName(buildPersonName(acspDataDto));

    if(acspDataDto.getDateOfBirth() != null) {
      stPersonalInformation.setBirthDate(acspDataDto.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    if(acspDataDto.getNationality() != null) {
      var nationalities = new ArrayList<String>();

      if (acspDataDto.getNationality().getFirstNationality() != null &&
              !acspDataDto.getNationality().getFirstNationality().isEmpty()) {
        nationalities.add(acspDataDto.getNationality().getFirstNationality());
      }
      if (acspDataDto.getNationality().getSecondNationality() != null &&
              !acspDataDto.getNationality().getSecondNationality().isEmpty()) {
        nationalities.add(acspDataDto.getNationality().getSecondNationality());
      }
      if (acspDataDto.getNationality().getThirdNationality() != null &&
              !acspDataDto.getNationality().getThirdNationality().isEmpty()) {
        nationalities.add(acspDataDto.getNationality().getThirdNationality());
      }
      stPersonalInformation.setNationalityOther(String.join(",", nationalities).toUpperCase());
    }

    stPersonalInformation.setUsualResidence(Optional.ofNullable(acspDataDto.getCountryOfResidence())
                              .map(String::toUpperCase).orElse(null));


    acsp.setStPersonalInformation(stPersonalInformation);
  }

  private void buildCompanyDetails(AcspDataDto acspDataDto, ACSP acsp) {

    if (acspDataDto.getCompanyDetails() != null) {
      acsp.setCompanyName(Optional.ofNullable(acspDataDto.getCompanyDetails().getCompanyName())
                            .map(String::toUpperCase).orElse(null));
    }
    if (acspDataDto.getTypeOfBusiness() != null) {
      switch (acspDataDto.getTypeOfBusiness()) {
        case lc, lp, llp:
          if (acspDataDto.getCompanyDetails() != null) {
            acsp.setCompanyNumber(Optional.ofNullable(acspDataDto.getCompanyDetails().getCompanyNumber())
                    .map(String::toUpperCase).orElse(null));
          }
          break;
        default:
         acsp.setBusinessName(Optional.ofNullable(acspDataDto.getBusinessName())
                            .map(String::toUpperCase).orElse(null));
      }
    }

  }

  private ServiceAddress buildServiceAddress(AcspDataDto acspDataDto, boolean isServiceAddressROA) {
    var serviceAddress = new ServiceAddress();
    if(acspDataDto != null) {
      serviceAddress.setCorrespondenceAddress(buildCorrespondenAddress(acspDataDto));
    }
    serviceAddress.setServiceAddressROA(isServiceAddressROA);
    return serviceAddress;
  }

  private Address buildCorrespondenAddress(AcspDataDto acspDataDto) {
    var correspondenceAddress = new Address();
    if(acspDataDto.getCorrespondenceAddress() != null) {
      correspondenceAddress.setAddressLine1(
              Optional.ofNullable(acspDataDto.getCorrespondenceAddress().getLine1())
                      .map(String::toUpperCase).orElse(null));
      correspondenceAddress.setAddressLine2(
              Optional.ofNullable(acspDataDto.getCorrespondenceAddress().getLine2())
                      .map(String::toUpperCase).orElse(null));
      correspondenceAddress.setPostalCode(
              Optional.ofNullable(acspDataDto.getCorrespondenceAddress().getPostcode())
                      .map(String::toUpperCase).orElse(null));
      correspondenceAddress.setCountry(
              Optional.ofNullable(acspDataDto.getCorrespondenceAddress().getCountry())
                      .map(String::toUpperCase).orElse(null));
      correspondenceAddress.setPremises(
              Optional.ofNullable(acspDataDto.getCorrespondenceAddress().getPropertyDetails())
                      .map(String::toUpperCase).orElse(null));
      correspondenceAddress.setRegion(
              Optional.ofNullable(acspDataDto.getCorrespondenceAddress().getCounty())
                      .map(String::toUpperCase).orElse(null));
    }
    return correspondenceAddress;
  }

  private Address buildBusinessAddress(AcspDataDto acspDataDto) {
    var businessAddress = new Address();
    if(acspDataDto.getBusinessAddress() != null) {
      businessAddress.setAddressLine1(
              Optional.ofNullable(acspDataDto.getBusinessAddress().getLine1())
                      .map(String::toUpperCase).orElse(null));
      businessAddress.setAddressLine2(
              Optional.ofNullable(acspDataDto.getBusinessAddress().getLine2())
                      .map(String::toUpperCase).orElse(null));
      businessAddress.setPostalCode(
              Optional.ofNullable(acspDataDto.getBusinessAddress().getPostcode())
                      .map(String::toUpperCase).orElse(null));
      businessAddress.setCountry(
              Optional.ofNullable(acspDataDto.getBusinessAddress().getCountry())
                      .map(String::toUpperCase).orElse(null));
      businessAddress.setPremises(
              Optional.ofNullable(acspDataDto.getBusinessAddress().getPropertyDetails())
                      .map(String::toUpperCase).orElse(null));
      businessAddress.setRegion(
              Optional.ofNullable(acspDataDto.getBusinessAddress().getCounty())
                      .map(String::toUpperCase).orElse(null));
    }
    return businessAddress;
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
          ACSP acsp,
          Transaction transaction,
          String passthroughTokenHeader)
          throws ServiceException {
    var paymentLink = transaction.getLinks().getPayment();
    var paymentReference = getPaymentReferenceFromTransaction(paymentLink, passthroughTokenHeader);
    var payment = getPayment(paymentReference, passthroughTokenHeader);

    acsp.setPaymentReference(paymentReference.toUpperCase());
    acsp.setPaymentMethod(payment.getPaymentMethod().toUpperCase());
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
