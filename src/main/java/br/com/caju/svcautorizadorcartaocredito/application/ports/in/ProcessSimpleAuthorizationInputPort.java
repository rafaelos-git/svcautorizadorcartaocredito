package br.com.caju.svcautorizadorcartaocredito.application.ports.in;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.TransactionPayload;

public interface ProcessSimpleAuthorizationInputPort {
    String process(TransactionPayload transactionPayload);
}
