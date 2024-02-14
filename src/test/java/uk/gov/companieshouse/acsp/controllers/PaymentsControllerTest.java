package uk.gov.companieshouse.acsp.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.acsp.Exception.ServiceException;
import uk.gov.companieshouse.acsp.service.PaymentsService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.payment.PaymentApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class PaymentsControllerTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private PaymentsService paymentsService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PaymentsController paymentsController;

    private static final String ID = "GFEDCBA";

    @Test
    void getPaymentDetails() throws ServiceException, IOException, URIValidationException {
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(ID);

        PaymentApi dummyPaymentDetails = new PaymentApi();
        dummyPaymentDetails.setAmount("100");

        when(transactionService.getTransaction(null, ID)).thenReturn(dummyTransaction);
        when(paymentsService.createPaymentSession(null, dummyTransaction)).thenReturn(dummyPaymentDetails);
        ResponseEntity<Object> response = paymentsController.getPaymentDetails(ID, request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dummyPaymentDetails,response.getBody());
    }

    @Test
    void getPaymentDetailsServiceException() throws ServiceException, IOException, URIValidationException {
        Transaction dummyTransaction = new Transaction();
        dummyTransaction.setId(ID);

        when(transactionService.getTransaction(null, ID)).thenReturn(dummyTransaction);
        doThrow(new ServiceException("ERROR", new IOException())).when(paymentsService)
                .createPaymentSession(null, dummyTransaction);

        var response = paymentsController.getPaymentDetails(ID, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

}