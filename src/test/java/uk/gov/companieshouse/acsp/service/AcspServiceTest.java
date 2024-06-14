package uk.gov.companieshouse.acsp.service;

import com.mongodb.MongoSocketReadException;
import com.mongodb.MongoSocketWriteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dao.AcspDataDao;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.repositories.AcspRepository;
import uk.gov.companieshouse.acsp.util.TransactionUtils;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.acsp.mapper.ACSPRegDataDtoDaoMapper;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcspServiceTest {
    private static final String REQUEST_ID = "fd4gld5h3jhh";
    private static final String SUBMISSION_ID = "demo@ch.gov.uk";
    private static final String USER_ID = "22334455";
    private static final String TRANSACTION_ID = "12345678";

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

    @Test
    void createAcspSuccess() throws Exception {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((AcspDataDao) any())).thenReturn(acspDataDao);
        doNothing().when(transactionService).updateTransaction(any(), any());
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID,
                USER_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createAcspDuplicateId() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((AcspDataDao) any())).thenThrow(DuplicateKeyException.class);
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID,
                USER_ID);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void createAcspException() {
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.insert((AcspDataDao) any())).thenThrow(MongoSocketWriteException.class);
        ResponseEntity<Object> response = acspService.createAcspRegData(transaction,
                acspData,
                REQUEST_ID,
                USER_ID);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void saveAcsp() throws Exception{
        AcspDataDto acspData = new AcspDataDto();
        acspData.setId("demo@ch.gov.uk");

        AcspDataDao acspDataDao = new AcspDataDao();
        acspDataDao.setId("demo@ch.gov.uk");
        when(acspRegDataDtoDaoMapper.dtoToDao(acspData)).thenReturn(acspDataDao);
        when(acspRepository.save(any())).thenReturn(acspDataDao);
        doNothing().when(transactionService).updateTransaction(any(), any());
        ResponseEntity<Object> response = acspService.saveAcspRegData(transaction,
                acspData,
                REQUEST_ID,
                USER_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getAcspApplicationCountGreaterThanOne() {
        int expectedApplicationCount = 3;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        when(acspRepository.countById(USER_ID)).thenReturn(expectedApplicationCount);
        ResponseEntity<Object> response = acspService.getAcspApplicationCount(USER_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void getAcspApplicationCountLessThanOne() {
        int expectedApplicationCount = 0;
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(acspRepository.countById(USER_ID)).thenReturn(expectedApplicationCount);
        ResponseEntity<Object> response = acspService.getAcspApplicationCount(USER_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void testGetSavedAcspWhenFoundSuccessfully() throws SubmissionNotLinkedToTransactionException {
        var acspDataDao = new AcspDataDao();
        acspDataDao.setId(SUBMISSION_ID);
        when(acspRepository.findById(SUBMISSION_ID)
        ).thenReturn(Optional.of(acspDataDao));
        when(acspRegDataDtoDaoMapper.daoToDto(acspDataDao)
        ).thenReturn(acspDataDto);

        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(String.class)))
                .thenReturn(true);
        var response = acspService.getAcsp(SUBMISSION_ID, transaction);
        assertNotNull(response);
    }

    @Test
    void testGetSavedAcspReturnsNullWhenNoLickedTransaction() {
        when(transactionUtils.isTransactionLinkedToAcspSubmission(eq(transaction), any(String.class)))
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
        when(acspRepository.findById(USER_ID)).thenReturn(Optional.of(new AcspDataDao()));
        ResponseEntity<Object> response = acspService.deleteAcspApplication(USER_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void deleteSavedApplicationNoApplicationFound() {
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(acspRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ResponseEntity<Object> response = acspService.deleteAcspApplication(USER_ID);
        assertEquals(expectedResponse, response);
    }

    @Test
    void deleteSavedApplicationErrorReadingFromDB() {
        ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(acspRepository.findById(USER_ID)).thenThrow(MongoSocketReadException.class);
        ResponseEntity<Object> response = acspService.deleteAcspApplication(USER_ID);
        assertEquals(expectedResponse, response);
    }
}