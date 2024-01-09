package uk.gov.companieshouse.acsp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.acsp.Exception.ServiceException;


import uk.gov.companieshouse.acsp.sdk.ApiClientService;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.payment.request.PaymentCreate;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.payment.PaymentSessionApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static uk.gov.companieshouse.acsp.util.Constants.PAYMENT_REQUIRED_HEADER;

@Service
public class PaymentsService {
    private final ApiClientService apiClientService;

    @Autowired
    public PaymentsService(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    public PaymentApi getPaymentsDetails(String passThroughHeader, Transaction transaction) throws ServiceException {
        PaymentSessionApi paymentSessionApi = new PaymentSessionApi();
        paymentSessionApi.setRedirectUri("https://cidev.aws.chdev.org/transactions/117524-754816-491724/company/10000025/confirmation");
        paymentSessionApi.setReference("company-accounts_TB32652391");
        paymentSessionApi.setResource("http://api.chs.local:4001/transactions/117524-754816-491724/payment");
        paymentSessionApi.setState("d7a6a1d8-0da6-4eec-9f2f-455dfa206e28");

        try {
            transaction.setStatus(TransactionStatus.CLOSED);
            var uri = "/transactions/" + transaction.getId();
            Map<String, Object> headers =
                    apiClientService.getApiClient()
                            .transactions().update(uri, transaction)
                            .execute().getHeaders();

            boolean paymentRequired = false;
            List<String> paymentRequiredHeaders = (ArrayList) headers.get(PAYMENT_REQUIRED_HEADER);

            if (paymentRequiredHeaders != null) {
                System.out.println(" Payment Entered : " + paymentRequiredHeaders.get(0) );
                PaymentCreate paymentCreate = null;
                try {
                    paymentCreate = apiClientService.getApiClient(passThroughHeader).payment().create("/payments",paymentSessionApi);

                } catch (IOException e) {
                    System.out.println(" Payment error : " + e.getStackTrace());
                    throw new RuntimeException(e);
                }
                ApiResponse<PaymentApi> paymentsAPI = paymentCreate.execute();
                System.out.println(" Payment Created : " + paymentsAPI.getData());
                return paymentsAPI.getData();
            }
            return null;
        } catch (URIValidationException e) {
            throw new ServiceException("Invalid URI for transactions resource", e);
        } catch (ApiErrorResponseException e) {
            throw new ServiceException("Error closing transaction", e);
        }
    }
}
