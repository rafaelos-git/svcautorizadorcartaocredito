package br.com.caju.svcautorizadorcartaocredito.adapters.out.repository;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.entity.AccountBalanceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountBalanceRepository extends MongoRepository<AccountBalanceEntity, String> {
    Optional<AccountBalanceEntity> findByAccountId(String accountId);
}
