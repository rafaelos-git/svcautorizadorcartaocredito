package br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.mapper;

import br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.entity.AccountBalanceEntity;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.AccountBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AccountBalanceMapperTest {

    private AccountBalanceMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(AccountBalanceMapper.class);
    }

    @Test
    public void testToAccountBalanceEntity() {
        AccountBalance accountBalance = new AccountBalance();
        accountBalance.setId("123");
        accountBalance.setAccountId("acc001");
        accountBalance.setBalanceFood(100.50);
        accountBalance.setBalanceMeal(200.75);
        accountBalance.setBalanceCash(300.25);

        AccountBalanceEntity entity = mapper.toAccountBalanceEntity(accountBalance);

        assertEquals("123", entity.get_id());
        assertEquals("acc001", entity.getAccountId());
        assertEquals(100.50, entity.getBalanceFood());
        assertEquals(200.75, entity.getBalanceMeal());
        assertEquals(300.25, entity.getBalanceCash());
    }

    @Test
    public void testToAccountBalance() {
        AccountBalanceEntity entity = new AccountBalanceEntity();
        entity.set_id("123");
        entity.setAccountId("acc001");
        entity.setBalanceFood(100.50);
        entity.setBalanceMeal(200.75);
        entity.setBalanceCash(300.25);

        AccountBalance accountBalance = mapper.toAccountBalance(entity);

        assertEquals("123", accountBalance.getId());
        assertEquals("acc001", accountBalance.getAccountId());
        assertEquals(100.50, accountBalance.getBalanceFood());
        assertEquals(200.75, accountBalance.getBalanceMeal());
        assertEquals(300.25, accountBalance.getBalanceCash());
    }

    @Test
    public void testToAccountBalanceEntityNull() {
        AccountBalanceEntity entity = mapper.toAccountBalanceEntity(null);

        assertNull(entity);
    }

    @Test
    public void testToAccountBalanceNull() {
        AccountBalance accountBalance = mapper.toAccountBalance(null);

        assertNull(accountBalance);
    }
}
