package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.enums.TypeOfBusiness;
import uk.gov.companieshouse.acsp.models.filing.*;
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
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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

  private LocalDate dateNow = LocalDate.now();

  private TransactionService transactionService;

  private final ApiClientService apiClientService;

  private AcspService acspService;

  private static final String PRESENTER = "presenter";

  private static final String SUBMISSION = "submission";

  private static final String PAYMENT_REFERENCE = "payment_reference";

  private static final String PAYMENT_METHOD = "payment_method";

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MM yyyy");



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
    LOGGER.info("starting generateAcspApplicationFiling--------------");
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
      setDescriptionFields(filing);
      buildFilingStatus(filing);
    }
  }

  private void buildFilingStatus(FilingApi filing) {
    filing.setKind(FILING_KIND_ACSP.toUpperCase());
    filing.setCost(costAmount);
  }

  private void buildPresenter(Map<String, Object>data, AcspDataDto acspDataDto) {
    var presenter = new Presenter();
    if (acspDataDto.getFirstName() != null) {
      presenter.setFirstName(acspDataDto.getFirstName().toUpperCase());
    }
    if (acspDataDto.getLastName() != null) {
      presenter.setLastName(acspDataDto.getLastName().toUpperCase());
    }
    if (acspDataDto.getId() != null) {
      presenter.setUserId(acspDataDto.getId().toUpperCase());
    }
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
    if(acspDataDto.getEmail() != null) {
      acsp.setEmail(acspDataDto.getEmail().toUpperCase());
    }
    if(acspDataDto.getTypeOfBusiness() == TypeOfBusiness.CORPORATE_BODY ||
        acspDataDto.getTypeOfBusiness() == TypeOfBusiness.PARTNERSHIP ||
        acspDataDto.getTypeOfBusiness() == TypeOfBusiness.UNINCORPORATED_ENTITY) {
      acsp.setOfficeAddress(buildBusinessAddress(acspDataDto));
    }
    if(acspDataDto.getBusinessAddress().equals(acspDataDto.getCorrespondenceAddress())) {
      acsp.setServiceAddressROA(true);
    } else {
      acsp.setCorrespondenceAddress(buildCorrespondenAddress(acspDataDto));
      acsp.setServiceAddressROA(false);
    }
    if(transaction.getStatus() != null &&
            transaction.getStatus().equals(TransactionStatus.CLOSED)) {
      setPaymentData(acsp, transaction, passThroughTokenHeader);
    }
    if (acspDataDto.getTypeOfBusiness() != null) {
      acsp.setAcspType(acspDataDto.getTypeOfBusiness().name().toUpperCase());
    }
    if(acspDataDto.getCompanyDetails() != null || acspDataDto.getBusinessName() != null) {
      buildCompanyDetails(acspDataDto, acsp);
    }
    if(acspDataDto.getWorkSector() != null) {
      acsp.setBusinessSector(acspDataDto.getWorkSector().toUpperCase());
    }

    if(acspDataDto.getAmlSupervisoryBodies() != null) {
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
      acsp.setAmlMemberships(amlMembershipsArray);
    }
    if(acspDataDto.getFirstName() != null || acspDataDto.getLastName() != null || acspDataDto.getMiddleName() != null) {
      PersonName personName = new PersonName();
      if(acspDataDto.getFirstName() != null) {
        personName.setFirstName(acspDataDto.getFirstName().toUpperCase());
      }
      if(acspDataDto.getLastName() != null) {
        personName.setLastName(acspDataDto.getLastName().toUpperCase());
      }
      if(acspDataDto.getMiddleName() != null) {
        personName.setMiddleName(acspDataDto.getMiddleName().toUpperCase());
      }
      acsp.setPersonName(personName);
    }


    if(acspDataDto.getTypeOfBusiness() != null && acspDataDto.getTypeOfBusiness().equals(TypeOfBusiness.SOLE_TRADER)) {
      var appointements = new Appointements();
      var officers = new Officers();

      if(acspDataDto.getFirstName() != null) {
        officers.setFirstName(acspDataDto.getFirstName().toUpperCase());
      }
      if(acspDataDto.getLastName() != null) {
        officers.setLastName(acspDataDto.getLastName().toUpperCase());
      }
      if(acspDataDto.getMiddleName() != null) {
        officers.setMiddleName(acspDataDto.getMiddleName().toUpperCase());
      }
      if(acspDataDto.getDateOfBirth() != null) {
        officers.setBirthDate(acspDataDto.getDateOfBirth().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      }

      if(acspDataDto.getNationality() != null) {
        var nationalities = new ArrayList<String>();

        if (acspDataDto.getNationality().getFirstNationality() != null) {
          nationalities.add(acspDataDto.getNationality().getFirstNationality());
        }
        if (acspDataDto.getNationality().getSecondNationality() != null) {
          nationalities.add(acspDataDto.getNationality().getSecondNationality());
        }
        if (acspDataDto.getNationality().getThirdNationality() != null) {
          nationalities.add(acspDataDto.getNationality().getThirdNationality());
        }
        officers.setNationalityOther(String.join(",", nationalities).toUpperCase());
      }


      if(acspDataDto.getCountryOfResidence() != null) {
        officers.setUsualResidence(acspDataDto.getCountryOfResidence().toUpperCase());
      }

      appointements.setOfficers(officers);
      acsp.setAppointements(appointements);
    }
    //item.setSubmissionLanguage(acspDataDto.getLanguage()); //add language in ascpDataModel
    return acsp;
  }


  private void buildCompanyDetails(AcspDataDto acspDataDto, ACSP acsp) {

    if (acspDataDto.getCompanyDetails() != null && acspDataDto.getCompanyDetails().getCompanyName() != null) {
      acsp.setCompanyName(acspDataDto.getCompanyDetails().getCompanyName().toUpperCase());
    }
    if (acspDataDto.getTypeOfBusiness() != null) {
      switch (acspDataDto.getTypeOfBusiness()) {
        case PARTNERSHIP, LIMITED_COMPANY, LIMITED_PARTNERSHIP :
          if (acspDataDto.getCompanyDetails() != null && acspDataDto.getCompanyDetails().getCompanyNumber() != null) {
            acsp.setCompanyNumber(acspDataDto.getCompanyDetails().getCompanyNumber().toUpperCase());
          }
          break;
        default:
          if (acspDataDto.getBusinessName() != null) {
            acsp.setBusinessName(acspDataDto.getBusinessName().toUpperCase());
          }
      }
      if (acspDataDto.getCompanyDetails() != null && acspDataDto.getCompanyDetails().getCompanyNumber() != null) {
        acsp.setCompanyNumber(acspDataDto.getCompanyDetails().getCompanyNumber().toUpperCase());
      }
    }

  }

  private Address buildCorrespondenAddress(AcspDataDto acspDataDto) {
    var correspondenceAddress = new Address();
    if(acspDataDto.getCorrespondenceAddresses() != null) {
      if (acspDataDto.getCorrespondenceAddresses().getLine1() != null) {
        correspondenceAddress.setAddressLine1(acspDataDto.getCorrespondenceAddresses().getLine1().toUpperCase());
      }
      if (acspDataDto.getCorrespondenceAddresses().getLine2() != null) {
        correspondenceAddress.setAddressLine2(acspDataDto.getCorrespondenceAddresses().getLine2().toUpperCase());
      }
      if (acspDataDto.getCorrespondenceAddresses().getPostcode() != null) {
        correspondenceAddress.setPostalCode(acspDataDto.getCorrespondenceAddresses().getPostcode().toUpperCase());
      }
      if (acspDataDto.getCorrespondenceAddresses().getCountry() != null) {
        correspondenceAddress.setCountry(acspDataDto.getCorrespondenceAddresses().getCountry().toUpperCase());
      }
      if (acspDataDto.getCorrespondenceAddresses().getPropertyDetails() != null) {
        correspondenceAddress.setPremises(acspDataDto.getCorrespondenceAddresses().getPropertyDetails().toUpperCase());
      }
      if (acspDataDto.getCorrespondenceAddresses().getCounty() != null) {
        correspondenceAddress.setRegion(acspDataDto.getCorrespondenceAddresses().getCounty().toUpperCase());
      }
    }
    return correspondenceAddress;
  }

  private Address buildBusinessAddress(AcspDataDto acspDataDto) {
    var businessAddress = new Address();
    if(acspDataDto.getBusinessAddress() != null) {
      if (acspDataDto.getBusinessAddress().getLine1() != null) {
        businessAddress.setAddressLine1(acspDataDto.getBusinessAddress().getLine1().toUpperCase());
      }
      if (acspDataDto.getBusinessAddress().getLine2() != null) {
        businessAddress.setAddressLine2(acspDataDto.getBusinessAddress().getLine2().toUpperCase());
      }
      if (acspDataDto.getBusinessAddress().getPostcode() != null) {
        businessAddress.setPostalCode(acspDataDto.getBusinessAddress().getPostcode().toUpperCase());
      }
      if (acspDataDto.getBusinessAddress().getCountry() != null) {
        businessAddress.setCountry(acspDataDto.getBusinessAddress().getCountry().toUpperCase());
      }
      if (acspDataDto.getBusinessAddress().getPropertyDetails() != null) {
        businessAddress.setPremises(acspDataDto.getBusinessAddress().getPropertyDetails().toUpperCase());
      }
      if (acspDataDto.getBusinessAddress().getCounty() != null) {
        businessAddress.setRegion(acspDataDto.getBusinessAddress().getCounty().toUpperCase());
      }
    }
    return businessAddress;
  }

  private void setDescriptionFields(FilingApi filing) {
    String formattedDate = dateNow.format(formatter);
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
