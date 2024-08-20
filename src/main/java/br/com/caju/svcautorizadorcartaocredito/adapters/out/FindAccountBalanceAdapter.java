package br.com.caju.svcautorizadorcartaocredito.adapters.out;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.AccountBalanceRepository;
import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.mapper.AccountBalanceMapper;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import br.com.caju.svcautorizadorcartaocredito.application.ports.out.FindAccountBalanceOutputPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class FindAccountBalanceAdapter implements FindAccountBalanceOutputPort {

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Autowired
    private AccountBalanceMapper accountBalanceMapper;

    @Override
    public Optional<AccountBalance> find(String accountId) {
        try {
            var accountBalanceEntity = accountBalanceRepository.findByAccountId(accountId);
            if (accountBalanceEntity.isPresent()) {
                log.info("Account found for accountId: {}", accountId);
            } else {
                log.warn("Account not found for accountId: {}", accountId);
            }
            return accountBalanceEntity.map(accountBalanceMapper::toAccountBalance);
        } catch (Exception e) {
            log.error("Error fetching account for accountId: {}", accountId, e);
            throw new RuntimeException("Error fetching account", e);
        }
    }
}