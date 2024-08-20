package br.com.caju.svcautorizadorcartaocredito.adapters.out;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.AccountBalanceRepository;
import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.mapper.AccountBalanceMapper;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.ProcessAuthorizationOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessAuthorizationAdapter implements ProcessAuthorizationOutputPort {

    @Autowired
    private AccountBalanceMapper accountBalanceMapper;

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Override
    public void process(AccountBalance accountBalance) {
        log.info("Updating account balance for accountId: {}", accountBalance.getAccountId());
        var accountBalanceEntity = accountBalanceMapper.toAccountBalanceEntity(accountBalance);
        try {
            accountBalanceRepository.save(accountBalanceEntity);
            log.info("Account balance updated successfully for accountId: {}", accountBalance.getAccountId());
        } catch (Exception e) {
            log.error("Failed to update account balance for accountId: {}", accountBalance.getAccountId(), e);
            throw new RuntimeException("Failed to update account balance", e);
        }
    }
}