package uk.gov.companieshouse.acsp.service;

import com.mongodb.MongoSocketWriteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.acsp.exception.InvalidTransactionStatusException;
import uk.gov.companieshouse.acsp.exception.ServiceException;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dao.Acsp;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dao.AcspDataSubmissionDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.models.dto.CompanyDto;
import uk.gov.companieshouse.acsp.models.enums.AcspType;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;
import uk.gov.companieshouse.acsp.util.TransactionUtils;
import uk.gov.companieshouse.api.model.transaction.Filing;
import uk.gov.companieshouse.api.model.transaction.Resource;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.acsp.mapper.ACSPRegDataDtoDaoMapper;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class AcspServiceTest {
    private static final String REQUEST_ID = "fd4gld5h3jhh";
    private static final String SUBMISSION_ID = "demo@ch.gov.uk";
    private static final String USER_ID = "22334455";
    private static final String TRANSACTION_ID = "12345678";

    private static final String PASS_THROUGH_HEADER = "passThoughHeader";

    @InjectMocks
    private AcspService acspService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private Transaction transaction;

    @Mock
    private AcspRepository acspRepository;

    @Mock
    private AcspDataDto acspDataDto;

    @Mock
    private ACSPRegDataDtoDaoMapper acspRegDataDtoDaoMapper;

    @Mock
    private TransactionUtils transactionUtils;

    private static class CompanyDetails extends CompanyDto {
        private String companyName;
        private String companyNumber;

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getCompanyNumber() {
            return companyNumber;
        }

        public void setCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
        }
    }

    @Test
    void createAcspSuccess() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        acspData.setAcspType(AcspType.REGISTER_ACSP);

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((Acsp) any())).thenReturn(acsp);
        doNothing().when(transactionService).updateTransaction(any(), any());
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createAcspDuplicateId() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        acspData.setAcspType(AcspType.REGISTER_ACSP);

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");

        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());

        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((Acsp) any())).thenThrow(DuplicateKeyException.class);
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void createAcspDuplicateApplication() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        acspData.setAcspType(AcspType.REGISTER_ACSP);


        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");

        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());

        transaction = new Transaction();

        Map<String, Resource> resourceMap = new HashMap<>();
        resourceMap.put("Resource 1", new Resource());

        transaction.setResources(resourceMap);

        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);

        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void createAcspException() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        acspData.setAcspType(AcspType.REGISTER_ACSP);

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((Acsp) any())).thenThrow(MongoSocketWriteException.class);
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void updateAcsp() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());

        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);
        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                USER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }


    @Test
    void updateAcspWhenTransactionisPendingPayment() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);
        when(transaction.getStatus()).thenReturn(TransactionStatus.CLOSED_PENDING_PAYMENT);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);
        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                USER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAcspFailWhenTransactionStatusIsClosed() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(transaction.getStatus()).thenReturn(TransactionStatus.CLOSED);

        assertThrows(InvalidTransactionStatusException.class, () -> {
            acspService.updateACSPDetails(transaction,
                    acspData,
                    REQUEST_ID,
                    USER_ID);
        });
    }

    @Test
    void updateAcspFailWhenTransactionStatusIsDeleted() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(transaction.getStatus()).thenReturn(TransactionStatus.DELETED);

        assertThrows(InvalidTransactionStatusException.class, () -> {
            acspService.updateACSPDetails(transaction,
                    acspData,
                    REQUEST_ID,
                    USER_ID);
        });
    }

    @Test
    void testUpdateAcspFailsWhenNoLickedTransaction() {
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(false);
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");

        assertThrows(SubmissionNotLinkedToTransactionException.class, () -> {
            acspService.updateACSPDetails(transaction,
                    acspData,
                    REQUEST_ID,
                    USER_ID);
        });
    }

    @Test
    void getAcspApplicationStatusNoApplication() {
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(acspRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ResponseEntity<Object> response = acspService.getAcspApplicationStatus(USER_ID, REQUEST_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void getAcspApplicationStatusOpenTransaction() throws ServiceException {
        var application = new AcspDataDao();
        var submissionData = new AcspDataSubmissionDao();
        Map<String, String> links = new HashMap<>();
        links.put("self", "/transaction/" + TRANSACTION_ID + "/");
        application.setLinks(links);
        application.setAcspDataSubmission(submissionData);
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.OPEN);
        var acsp = new Acsp();
        acsp.setAcspDataDao(application);
        acsp.setId(application.getId());

        when(acspRepository.findById(USER_ID)).thenReturn(Optional.of(acsp));
        when(transactionService.getTransaction(REQUEST_ID, TRANSACTION_ID)).thenReturn(transaction);
        ResponseEntity<Object> response = acspService.getAcspApplicationStatus(USER_ID, REQUEST_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void getAcspApplicationStatusTransactionClosedAndRejected() throws ServiceException {
        var application = new AcspDataDao();
        var submissionData = new AcspDataSubmissionDao();
        Map<String, String> links = new HashMap<>();
        links.put("self", "/transaction/" + TRANSACTION_ID + "/");
        application.setLinks(links);
        application.setAcspDataSubmission(submissionData);
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.CLOSED);
        Map<String, Filing> filingsMap = new HashMap<>();
        Filing filing = new Filing();
        filing.setStatus("rejected");
        filingsMap.put(TRANSACTION_ID + "-1", filing);
        transaction.setFilings(filingsMap);
        var acsp = new Acsp();
        acsp.setAcspDataDao(application);
        acsp.setId(application.getId());

        when(acspRepository.findById(USER_ID)).thenReturn(Optional.of(acsp));
        when(transactionService.getTransaction(REQUEST_ID, TRANSACTION_ID)).thenReturn(transaction);
        ResponseEntity<Object> response = acspService.getAcspApplicationStatus(USER_ID, REQUEST_ID);
        assertEquals(expectedResponse, response);
        verify(acspRepository, times(1)).delete(acsp);
    }

    @Test
    void getAcspApplicationStatusTransactionClosedAndProcessing() throws ServiceException {
        var application = new AcspDataDao();
        var submissionData = new AcspDataSubmissionDao();
        Map<String, String> links = new HashMap<>();
        links.put("self", "/transaction/" + TRANSACTION_ID + "/");
        application.setLinks(links);
        application.setAcspDataSubmission(submissionData);
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.CLOSED);
        Map<String, Filing> filingsMap = new HashMap<>();
        Filing filing = new Filing();
        filing.setStatus("processing");
        filingsMap.put(TRANSACTION_ID + "-1", filing);
        transaction.setFilings(filingsMap);
        var acsp = new Acsp();
        acsp.setAcspDataDao(application);
        acsp.setId(application.getId());

        when(acspRepository.findById(USER_ID)).thenReturn(Optional.of(acsp));
        when(transactionService.getTransaction(REQUEST_ID, TRANSACTION_ID)).thenReturn(transaction);
        ResponseEntity<Object> response = acspService.getAcspApplicationStatus(USER_ID, REQUEST_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void getAcspApplicationStatusError() throws ServiceException {
        var application = new AcspDataDao();
        var submissionData = new AcspDataSubmissionDao();
        Map<String, String> links = new HashMap<>();
        links.put("self", "/transaction/" + TRANSACTION_ID + "/");
        application.setLinks(links);
        application.setAcspDataSubmission(submissionData);
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        var acsp = new Acsp();
        acsp.setAcspDataDao(application);
        acsp.setId(application.getId());

        when(acspRepository.findById(USER_ID)).thenReturn(Optional.of(acsp));
        doThrow(ServiceException.class).when(transactionService).getTransaction(REQUEST_ID, TRANSACTION_ID);
        ResponseEntity<Object> response = acspService.getAcspApplicationStatus(USER_ID, REQUEST_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testGetSavedAcspWhenFoundSuccessfully() throws SubmissionNotLinkedToTransactionException {
        var acspDataDao = new AcspDataDao();
        acspDataDao.setId(SUBMISSION_ID);
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(acspRepository.findById(SUBMISSION_ID)
        ).thenReturn(Optional.of(acsp));
        when(acspRegDataDtoDaoMapper.daoToDto(acspDataDao)
        ).thenReturn(acspDataDto);

        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        var response = acspService.getAcsp(SUBMISSION_ID, transaction);
        assertNotNull(response);
    }

    @Test
    void testGetSavedAcspReturnsNullWhenNoLickedTransaction() {
        var acspDataDao = new AcspDataDao();
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        acspDataDao.setId(SUBMISSION_ID);
        when(acspRepository.findById(SUBMISSION_ID)).thenReturn(Optional.of(acsp));
        when(acspRegDataDtoDaoMapper.daoToDto(acspDataDao)).thenReturn(acspDataDto);
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(false);
        transaction.setId("1234");
        assertThrows(SubmissionNotLinkedToTransactionException.class, () -> {
            acspService.getAcsp(SUBMISSION_ID, transaction);
        });
    }

    private Transaction buildTransaction() {
        Transaction transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);
        return transaction;
    }

    @Test
    void deleteSavedApplicationSuccess() {
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        ResponseEntity<Object> response = acspService.deleteAcspApplication(USER_ID);
        verify(acspRepository, times(1)).deleteById(USER_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void deleteSavedApplicationErrorReadingFromDB() {
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        doThrow(MongoSocketWriteException.class).when(acspRepository).deleteById(USER_ID);
        ResponseEntity<Object> response = acspService.deleteAcspApplication(USER_ID);
        assertEquals(expectedResponse, response);
    }


    @Test
    void updateAcspCompanyDetails() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setCompanyDetails(new CompanyDetails());
        acspData.getCompanyDetails().setCompanyName("Test Company");
        acspData.getCompanyDetails().setCompanyNumber("12345678");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction).setCompanyName("Test Company");
        verify(transaction).setCompanyNumber("12345678");
        verify(transactionService).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void noUpdateToTransactionCompanyNameNull() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setCompanyDetails(new CompanyDetails());
        acspData.getCompanyDetails().setCompanyName(null);
        acspData.getCompanyDetails().setCompanyNumber("12345678");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction, times(0)).setCompanyName(null);
        verify(transaction, times(0)).setCompanyNumber("12345678");
        verify(transactionService, times(0)).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void noUpdateToTransactionCompanyNumberNull() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setCompanyDetails(new CompanyDetails());
        acspData.getCompanyDetails().setCompanyName("Test Company");
        acspData.getCompanyDetails().setCompanyNumber(null);

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction, times(0)).setCompanyName("Test Company");
        verify(transaction, times(0)).setCompanyNumber(null);
        verify(transactionService, times(0)).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAcspCompanyDetailsRemainTheSame() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setCompanyDetails(new CompanyDetails());
        acspData.getCompanyDetails().setCompanyName("Test Company");
        acspData.getCompanyDetails().setCompanyNumber("12345678");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);
        when(transaction.getCompanyName()).thenReturn("Test Company");
        when(transaction.getCompanyNumber()).thenReturn("12345678");

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction, times(0)).setCompanyName("Test Company");
        verify(transaction, times(0)).setCompanyNumber("12345678");
        verify(transactionService, times(0)).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAcspCompanyDetailsCompanyNumberChange() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setCompanyDetails(new CompanyDetails());
        acspData.getCompanyDetails().setCompanyName("Test Company");
        acspData.getCompanyDetails().setCompanyNumber("12345678");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);
        when(transaction.getCompanyName()).thenReturn("Test Company");
        when(transaction.getCompanyNumber()).thenReturn("12345679");

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction).setCompanyName("Test Company");
        verify(transaction).setCompanyNumber("12345678");
        verify(transactionService).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAcspBusinessName() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setBusinessName("Test Business Name");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction).setCompanyName("Test Business Name");
        verify(transaction).setCompanyNumber(null);
        verify(transactionService).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAcspBusinessNameRemainsTheSame() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setBusinessName("Test Business Name");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);
        when(transaction.getCompanyName()).thenReturn("Test Business Name");

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction, times(0)).setCompanyName("Test Business Name");
        verify(transactionService, times(0)).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateAcspWithNullCompanyDetailsAndBusinessName() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        // No companyDetails or businessName set
        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(AcspDataDto.class)))
                .thenReturn(true);
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acsp);

        when(transaction.getStatus()).thenReturn(TransactionStatus.OPEN);

        ResponseEntity<Object> response = acspService.updateACSPDetails(transaction,
                acspData,
                REQUEST_ID,
                "demo@ch.gov.uk");
        verify(transaction, times(0)).setCompanyName(anyString());
        verify(transaction, times(0)).setCompanyNumber(anyString());
        verify(transactionService, times(0)).updateTransaction((REQUEST_ID), (transaction));

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteAcspApplicationAndTransactionSuccess() throws ServiceException {

        doNothing().when(acspRepository).deleteById(USER_ID);
        doNothing().when(transactionService).deleteTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID);

        ResponseEntity<Object> response = acspService.deleteAcspApplicationAndTransaction(PASS_THROUGH_HEADER, USER_ID, TRANSACTION_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(acspRepository, times(1)).deleteById(USER_ID);
        verify(transactionService, times(1)).deleteTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID);
    }

    @Test
    void deleteAcspApplicationAndTransactionErrorApplication() throws ServiceException {
        doThrow(MongoSocketWriteException.class).when(acspRepository).deleteById(USER_ID);

        ResponseEntity<Object> response = acspService.deleteAcspApplicationAndTransaction(PASS_THROUGH_HEADER, USER_ID, TRANSACTION_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(acspRepository, times(1)).deleteById(USER_ID);
        verify(transactionService, times(0)).deleteTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID);
    }

    @Test
    void deleteAcspApplicationAndTransactionErrorDeletingTransaction() throws ServiceException {

        doNothing().when(acspRepository).deleteById(USER_ID);
        doThrow(ServiceException.class).when(transactionService).deleteTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID);

        ResponseEntity<Object> response = acspService.deleteAcspApplicationAndTransaction(PASS_THROUGH_HEADER, USER_ID, TRANSACTION_ID);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(acspRepository, times(1)).deleteById(USER_ID);
        verify(transactionService, times(1)).deleteTransaction(PASS_THROUGH_HEADER, TRANSACTION_ID);
    }

    @Test
    void createUpdateAcspSuccess() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        acspData.setAcspType(AcspType.UPDATE_ACSP);

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((Acsp) any())).thenReturn(acsp);
        doNothing().when(transactionService).updateTransaction(any(), any());
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createUpdateAcspDuplicateId() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        acspData.setAcspType(AcspType.UPDATE_ACSP);

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");

        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());

        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((Acsp) any())).thenThrow(DuplicateKeyException.class);
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void createUpdateAcspDuplicateApplication() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");
        acspData.setAcspType(AcspType.UPDATE_ACSP);


        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");

        var acsp = new Acsp();
        acsp.setAcspDataDao(acspDataDao);
        acsp.setId(acspDataDao.getId());

        transaction = new Transaction();

        Map<String, Resource> resourceMap = new HashMap<>();
        resourceMap.put("Resource 1", new Resource());

        transaction.setResources(resourceMap);

        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);

        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
