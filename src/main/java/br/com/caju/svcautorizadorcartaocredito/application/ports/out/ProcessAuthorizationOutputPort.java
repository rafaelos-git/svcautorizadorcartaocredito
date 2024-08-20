package br.com.caju.svcautorizadorcartaocredito.application.ports.out;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;

public interface ProcessAuthorizationOutputPort {
    void process(AccountBalance accountBalance);
}
