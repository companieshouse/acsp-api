package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.filing.*;
import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.filinggenerator.FilingApi;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.acsp.util.Constants.FILING_KIND_ACSP;

@Service
public class FilingsService {

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

  private static final String ITEM = "item";

  private static final String ACSP = "data";

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
          throws ServiceException {
    var filing = new FilingApi();
    setFilingApiData(filing, acspApplicationId, transactionId, passThroughTokenHeader);
    return filing;
  }

  private void setFilingApiData(FilingApi filing, String acspApplicationId, String transactionId,
                                String passThroughTokenHeader) throws ServiceException {
    if(acspService.getAcsp(acspApplicationId).isPresent()) {
      var acspDataDto = acspService.getAcsp(acspApplicationId).get();
      var data = new HashMap<String, Object>();
      var transaction = transactionService.getTransaction(passThroughTokenHeader, transactionId);
      buildPresenter(data, acspDataDto);
      buildSubmission(data, acspDataDto, transactionId);
      buildAcspData(data, acspDataDto);
      buildItem(data, transactionId);
      setPaymentData(data, transaction, passThroughTokenHeader);
      setDescriptionFields(filing);
      buildFilingStatus(filing, data);
    }
  }

  private void buildFilingStatus(FilingApi filing, HashMap<String, Object> data) {
    filing.setKind(FILING_KIND_ACSP);
    filing.setData(data);
    filing.setCost(costAmount);
  }

  private void buildPresenter(HashMap<String, Object>data, AcspDataDto acspDataDto) {
    var presenter = new Presenter();
    presenter.setFirstName(acspDataDto.getFirstName().toUpperCase());
    presenter.setLastName(acspDataDto.getLastName().toUpperCase());
    presenter.setUserId(acspDataDto.getId().toUpperCase());
    //presenter.setLanguage(); //add language in ascpDataModel
    data.put(PRESENTER, presenter);
  }


  private void buildSubmission(HashMap<String, Object>data, AcspDataDto acspDataDto, String transactionId) {
    var submission = new Submission();
    submission.setReceivedAt(acspDataDto.getAcspDataSubmission().getUpdatedAt());
    submission.setTransactionId(transactionId.toUpperCase());
    data.put(SUBMISSION, submission);
  }

  private void buildItem(HashMap<String, Object>data, String transactionId) {
    var item = new Item();
    item.setKind(FILING_KIND_ACSP.toUpperCase());
    item.setSubmissionId(transactionId.toUpperCase());
    //item.setSubmissionLanguage(acspDataDto.getLanguage()); //add language in ascpDataModel
    data.put(ITEM, item);
  }

  private void buildAcspData(HashMap<String, Object>data, AcspDataDto acspDataDto) {
    var acsp = new ACSP();
    acsp.setEmail(acspDataDto.getEmail().toUpperCase());

    acsp.setCorrespondenceAddress1(buildCorrespondenAddress(acspDataDto));
    acsp.setOfficeAddress(buildBusinessAddress(acspDataDto));
    acsp.setPaymentReference(PAYMENT_REFERENCE);
    acsp.setPaymentMethod("credit-card");
    //item.setSubmissionLanguage(acspDataDto.getLanguage()); //add language in ascpDataModel
    data.put(ACSP, acsp);
  }

  private Address buildCorrespondenAddress(AcspDataDto acspDataDto) {
    var correspondenceAddress = new Address();
    correspondenceAddress.setAddressLine1(acspDataDto.getCorrespondenceAddresses().getLine1().toUpperCase());
    correspondenceAddress.setAddressLine2(acspDataDto.getCorrespondenceAddresses().getLine2().toUpperCase());
    correspondenceAddress.setPostalCode(acspDataDto.getCorrespondenceAddresses().getPostcode().toUpperCase());
    correspondenceAddress.setCountry(acspDataDto.getCorrespondenceAddresses().getCountry().toUpperCase());
    correspondenceAddress.setPremises(acspDataDto.getCorrespondenceAddresses().getPropertyDetails().toUpperCase());
    correspondenceAddress.setRegion(acspDataDto.getCorrespondenceAddresses().getCounty().toUpperCase());
    return correspondenceAddress;
  }

  private Address buildBusinessAddress(AcspDataDto acspDataDto) {
    var businessAddress = new Address();
    businessAddress.setAddressLine1(acspDataDto.getBusinessAddress().getLine1().toUpperCase());
    businessAddress.setAddressLine2(acspDataDto.getBusinessAddress().getLine2().toUpperCase());
    businessAddress.setPostalCode(acspDataDto.getBusinessAddress().getPostcode().toUpperCase());
    businessAddress.setCountry(acspDataDto.getBusinessAddress().getCountry().toUpperCase());
    businessAddress.setPremises(acspDataDto.getBusinessAddress().getPropertyDetails().toUpperCase());
    businessAddress.setRegion(acspDataDto.getBusinessAddress().getCounty().toUpperCase());
    return businessAddress;
  }

  private void setDescriptionFields(FilingApi filing) {
    String formattedDate = dateNow.format(formatter);
    filing.setDescriptionIdentifier(filingDescriptionIdentifier.toUpperCase());
    filing.setDescription(filingDescription.replace("{date}", formattedDate).toUpperCase());
    Map<String, String> values = new HashMap<>();
    filing.setDescriptionValues(values);
  }


  private void setPaymentData(
          Map<String, Object> data,
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
