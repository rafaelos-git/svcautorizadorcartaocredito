package br.com.caju.svcautorizadorcartaocredito.adapters.in.controller;

import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.mapper.TransactionPayloadMapper;
import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.request.TransactionPayloadRequest;
import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.response.AuthorizationResponse;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.TransactionPayload;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.ProcessFallbackAuthorizationInputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.ProcessSimpleAuthorizationInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.*;

@WebMvcTest(ProcessAuthorizationController.class)
public class ProcessAuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionPayloadMapper transactionPayloadMapper;

    @MockBean
    private ProcessSimpleAuthorizationInputPort processSimpleAuthorizationInputPort;

    @MockBean
    private ProcessFallbackAuthorizationInputPort processFallbackAuthorizationInputPort;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionPayloadRequest transactionPayloadRequest;
    private TransactionPayload transactionPayload;

    @BeforeEach
    public void setup() {
        transactionPayloadRequest = new TransactionPayloadRequest();
        transactionPayloadRequest.setAccount("123");
        transactionPayloadRequest.setMcc("5411");
        transactionPayloadRequest.setTotalAmount(100.0);
        transactionPayloadRequest.setMerchant("Merchant");
        transactionPayload = new TransactionPayload();
        transactionPayload.setAccount("123");
        transactionPayload.setMcc("5411");
        transactionPayload.setTotalAmount(100.0);
        transactionPayload.setMerchant("Merchant");
        AuthorizationResponse authorizationResponse = new AuthorizationResponse("00");
    }

    @Test
    public void testProcessSimpleAuthorization() throws Exception {
        when(transactionPayloadMapper.toTransactionPayload(transactionPayloadRequest)).thenReturn(transactionPayload);
        when(processSimpleAuthorizationInputPort.process(transactionPayload)).thenReturn("00");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/simple-authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionPayloadRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("00"));

        verify(transactionPayloadMapper, times(1)).toTransactionPayload(transactionPayloadRequest);
        verify(processSimpleAuthorizationInputPort, times(1)).process(transactionPayload);
    }

    @Test
    public void testProcessFallbackAuthorization() throws Exception {
        when(transactionPayloadMapper.toTransactionPayload(transactionPayloadRequest)).thenReturn(transactionPayload);
        when(processFallbackAuthorizationInputPort.process(transactionPayload)).thenReturn("00");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/fallback-authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionPayloadRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("00"));

        verify(transactionPayloadMapper, times(1)).toTransactionPayload(transactionPayloadRequest);
        verify(processFallbackAuthorizationInputPort, times(1)).process(transactionPayload);
    }

    @Test
    public void testProcessSimpleAuthorizationWithValidationError() throws Exception {
        TransactionPayloadRequest invalidRequest = new TransactionPayloadRequest();
        transactionPayloadRequest.setAccount("");
        transactionPayloadRequest.setMcc("");
        transactionPayloadRequest.setTotalAmount(-100.0);
        transactionPayloadRequest.setMerchant(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/simple-authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testProcessFallbackAuthorizationWithValidationError() throws Exception {
        TransactionPayloadRequest invalidRequest = new TransactionPayloadRequest();
        transactionPayloadRequest.setAccount("");
        transactionPayloadRequest.setMcc("");
        transactionPayloadRequest.setTotalAmount(-100.0);
        transactionPayloadRequest.setMerchant(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions/fallback-authorizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
