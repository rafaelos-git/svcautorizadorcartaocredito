package br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AccountBalanceEntityTest {

    @Test
    public void testGettersAndSetters() {
        AccountBalanceEntity entity = new AccountBalanceEntity();

        String id = "123";
        String accountId = "acc001";
        double balanceFood = 100.50;
        double balanceMeal = 200.75;
        double balanceCash = 300.25;

        entity.set_id(id);
        entity.setAccountId(accountId);
        entity.setBalanceFood(balanceFood);
        entity.setBalanceMeal(balanceMeal);
        entity.setBalanceCash(balanceCash);

        assertEquals(id, entity.get_id());
        assertEquals(accountId, entity.getAccountId());
        assertEquals(balanceFood, entity.getBalanceFood());
        assertEquals(balanceMeal, entity.getBalanceMeal());
        assertEquals(balanceCash, entity.getBalanceCash());
    }

    @Test
    public void testDefaultConstructor() {
        AccountBalanceEntity entity = new AccountBalanceEntity();

        assertNull(entity.get_id());
        assertNull(entity.getAccountId());
        assertEquals(0.0, entity.getBalanceFood());
        assertEquals(0.0, entity.getBalanceMeal());
        assertEquals(0.0, entity.getBalanceCash());
    }
}
