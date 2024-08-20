package br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.mapper;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.entity.AccountBalanceEntity;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountBalanceMapper {
    @Mapping(source = "id", target = "_id")
    AccountBalanceEntity toAccountBalanceEntity(AccountBalance accountBalance);

    @Mapping(source = "_id", target = "id")
    AccountBalance toAccountBalance(AccountBalanceEntity accountBalanceEntity);
}
