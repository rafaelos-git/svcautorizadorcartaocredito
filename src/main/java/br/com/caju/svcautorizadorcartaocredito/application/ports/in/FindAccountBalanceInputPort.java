package br.com.caju.svcautorizadorcartaocredito.application.ports.in;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;

public interface FindAccountBalanceInputPort {
    AccountBalance find(String account);
}
