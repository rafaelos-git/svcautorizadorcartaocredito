package br.com.caju.svcautorizadorcartaocredito.application.core.usecase;

import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import br.com.caju.svcautorizadorcartaocredito.application.ports.in.FindAccountBalanceInputPort;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.FindAccountBalanceOutputPort;

public class FindAccountBalanceUseCase implements FindAccountBalanceInputPort {
    private final FindAccountBalanceOutputPort findAccountBalanceOutputPort;

    public FindAccountBalanceUseCase(
            FindAccountBalanceOutputPort findAccountBalanceOutputPort
    ) {
        this.findAccountBalanceOutputPort = findAccountBalanceOutputPort;
    }

    @Override
    public AccountBalance find(String account) {
        return findAccountBalanceOutputPort.find(account)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}
