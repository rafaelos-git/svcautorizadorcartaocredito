package br.com.caju.svcautorizadorcartaocredito.application.ports.out;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;

import java.util.Optional;

public interface FindAccountBalanceOutputPort {
    Optional<AccountBalance> find(String account);
}
