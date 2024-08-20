package br.com.caju.svcautorizadorcartaocredito.application.ports.in;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.TransactionPayload;

public interface ProcessFallbackAuthorizationInputPort {
    String process(TransactionPayload transactionPayload);
}
