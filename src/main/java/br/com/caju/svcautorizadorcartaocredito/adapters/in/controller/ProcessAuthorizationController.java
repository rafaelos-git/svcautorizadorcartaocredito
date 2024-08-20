package br.com.caju.svcautorizadorcartaocredito.adapters.in.controller;

import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.mapper.TransactionPayloadMapper;
import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.request.TransactionPayloadRequest;
import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.response.AuthorizationResponse;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.ProcessFallbackAuthorizationInputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.ProcessSimpleAuthorizationInputPort;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
public class ProcessAuthorizationController {

    @Autowired
    private TransactionPayloadMapper transactionPayloadMapper;

    @Autowired
    private ProcessSimpleAuthorizationInputPort processSimpleAuthorizationInputPort;

    @Autowired
    private ProcessFallbackAuthorizationInputPort processFallbackAuthorizationInputPort;

    @PostMapping("/simple-authorizations")
    public ResponseEntity<AuthorizationResponse> processSimple(@Valid @RequestBody TransactionPayloadRequest transactionPayloadRequest) {
        log.info("Received request for simple authorization. Request data: {}", transactionPayloadRequest);

        var transactionPayload = transactionPayloadMapper.toTransactionPayload(transactionPayloadRequest);
        log.debug("Mapped TransactionPayload: {}", transactionPayload);

        String responseCode = processSimpleAuthorizationInputPort.process(transactionPayload);
        log.info("Processed simple authorization. Response code: {}", responseCode);

        AuthorizationResponse response = new AuthorizationResponse(responseCode);
        log.debug("Authorization response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/fallback-authorizations")
    public ResponseEntity<AuthorizationResponse> processFallback(@Valid @RequestBody TransactionPayloadRequest transactionPayloadRequest) {
        log.info("Received request for fallback authorization. Request data: {}", transactionPayloadRequest);

        var transactionPayload = transactionPayloadMapper.toTransactionPayload(transactionPayloadRequest);
        log.debug("Mapped TransactionPayload: {}", transactionPayload);

        String responseCode = processFallbackAuthorizationInputPort.process(transactionPayload);
        log.info("Processed fallback authorization. Response code: {}", responseCode);

        AuthorizationResponse response = new AuthorizationResponse(responseCode);
        log.debug("Authorization response: {}", response);

        return ResponseEntity.ok(response);
    }
}