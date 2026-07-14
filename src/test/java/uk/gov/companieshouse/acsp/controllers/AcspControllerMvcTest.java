package uk.gov.companieshouse.acsp.controllers;

import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import uk.gov.companieshouse.acsp.exception.SubmissionNotLinkedToTransactionException;
import uk.gov.companieshouse.acsp.models.dto.AcspDataDto;
import uk.gov.companieshouse.acsp.service.AcspService;
import uk.gov.companieshouse.acsp.service.TransactionService;
import uk.gov.companieshouse.acsp.testutil.AuthTestUtil;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.model.transaction.TransactionStatus;

import java.net.URI;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.companieshouse.acsp.testutil.AuthTestUtil.PASSTHROUGH_HEADER;

@WebMvcTest(controllers = AcspController.class)
class AcspControllerMvcTest {

    private static final String TRANSACTION_ID = "324234-123123-768685";
    private static final String ACSP_APPLICATION_ID = "abc123";

    private static final String BASE_URI =
            "/transactions/" + TRANSACTION_ID + "/authorised-corporate-service-provider-applications";
    private static final String RESOURCE_URI = BASE_URI + "/" + ACSP_APPLICATION_ID;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @MockitoBean
    private AcspService acspService;

    @MockitoBean
    private TransactionService transactionService;

    private Transaction transaction;
    private AcspDataDto acspDataDto;

    @BeforeEach
    void setUp() throws Exception {
        transaction = new Transaction();
        transaction.setId(TRANSACTION_ID);

        acspDataDto = new AcspDataDto();
        acspDataDto.setId(ACSP_APPLICATION_ID);
        acspDataDto.setBusinessName("Test Business");

        // TransactionInterceptor populates the transaction request attribute from this call
        when(transactionService.getTransaction(anyString(), eq(TRANSACTION_ID))).thenReturn(transaction);
    }

    @Test
    void createAcspDataReturnsCreated() throws Exception {
        when(acspService.createAcspRegData(any(Transaction.class), any(AcspDataDto.class), anyString()))
                .thenReturn(ResponseEntity.created(URI.create(RESOURCE_URI)).body(acspDataDto));

        ResultActions result = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
                .content(jsonMapper.writeValueAsString(acspDataDto)));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").value(acspDataDto.getId()));
        result.andExpect(jsonPath("$.business_name").value(acspDataDto.getBusinessName()));
        result.andExpect(jsonPath("$.verified").doesNotExist());
    }

    @Test
    void createAcspDataReturnsBadRequestWhenServiceThrows() throws Exception {
        when(acspService.createAcspRegData(any(Transaction.class), any(AcspDataDto.class), anyString()))
                .thenThrow(new RuntimeException("boom"));

        ResultActions result = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
                .content(jsonMapper.writeValueAsString(acspDataDto)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void saveAcspDataReturnsOk() throws Exception {
        when(acspService.updateACSPDetails(any(Transaction.class), any(AcspDataDto.class), anyString(), eq(ACSP_APPLICATION_ID)))
                .thenReturn(ResponseEntity.ok().body(acspDataDto));

        ResultActions result = mockMvc.perform(put(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
                .content(jsonMapper.writeValueAsString(acspDataDto)));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(acspDataDto.getId()));
        result.andExpect(jsonPath("$.business_name").value(acspDataDto.getBusinessName()));
    }

    @Test
    void saveAcspDataReturnsBadRequestWhenServiceThrows() throws Exception {
        when(acspService.updateACSPDetails(any(Transaction.class), any(AcspDataDto.class), anyString(), eq(ACSP_APPLICATION_ID)))
                .thenThrow(new SubmissionNotLinkedToTransactionException("not linked"));

        ResultActions result = mockMvc.perform(put(RESOURCE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
                .content(jsonMapper.writeValueAsString(acspDataDto)));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void getAcspDataReturnsOk() throws Exception {
        when(acspService.getAcsp(eq(ACSP_APPLICATION_ID), any(Transaction.class)))
                .thenReturn(Optional.of(acspDataDto));

        ResultActions result = mockMvc.perform(get(RESOURCE_URI)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
        );

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(acspDataDto.getId()));
        result.andExpect(jsonPath("$.business_name").value(acspDataDto.getBusinessName()));
    }

    @Test
    void getAcspDataReturnsNotFoundWhenEmpty() throws Exception {
        when(acspService.getAcsp(eq(ACSP_APPLICATION_ID), any(Transaction.class)))
                .thenReturn(Optional.empty());

        ResultActions result = mockMvc.perform(get(RESOURCE_URI)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    void getAcspDataReturnsBadRequestWhenNotLinked() throws Exception {
        when(acspService.getAcsp(eq(ACSP_APPLICATION_ID), any(Transaction.class)))
                .thenThrow(new SubmissionNotLinkedToTransactionException("not linked"));

        ResultActions result = mockMvc.perform(get(RESOURCE_URI)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
        );

        result.andExpect(status().isBadRequest());
    }

    @Test
    void deleteApplicationWhenTransactionClosed() throws Exception {
        transaction.setStatus(TransactionStatus.CLOSED);
        when(acspService.deleteAcspApplication(ACSP_APPLICATION_ID))
                .thenReturn(new ResponseEntity<>(NO_CONTENT));

        ResultActions result = mockMvc.perform(delete(RESOURCE_URI)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
        );

        result.andExpect(status().isNoContent());
        verify(acspService, times(1)).deleteAcspApplication(ACSP_APPLICATION_ID);
    }

    @Test
    void deleteApplicationWhenTransactionOpen() throws Exception {
        transaction.setStatus(TransactionStatus.OPEN);
        when(acspService.deleteAcspApplicationAndTransaction(PASSTHROUGH_HEADER, ACSP_APPLICATION_ID, TRANSACTION_ID))
                .thenReturn(new ResponseEntity<>(NO_CONTENT));

        ResultActions result = mockMvc.perform(delete(RESOURCE_URI)
                .headers(AuthTestUtil.getOauth2AuthorisationHeaders())
        );

        result.andExpect(status().isNoContent());
        verify(acspService, times(1))
                .deleteAcspApplicationAndTransaction(PASSTHROUGH_HEADER, ACSP_APPLICATION_ID, TRANSACTION_ID);
    }
}