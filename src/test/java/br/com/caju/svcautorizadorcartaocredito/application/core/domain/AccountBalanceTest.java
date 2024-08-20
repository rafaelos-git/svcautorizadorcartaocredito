package br.com.caju.svcautorizadorcartaocredito.application.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountBalanceTest {

    @Test
    public void testGettersAndSetters() {
        AccountBalance accountBalance = new AccountBalance();

        String expectedId = "id123";
        String expectedAccountId = "account123";
        double expectedBalanceFood = 100.0;
        double expectedBalanceMeal = 50.0;
        double expectedBalanceCash = 200.0;

        accountBalance.setId(expectedId);
        accountBalance.setAccountId(expectedAccountId);
        accountBalance.setBalanceFood(expectedBalanceFood);
        accountBalance.setBalanceMeal(expectedBalanceMeal);
        accountBalance.setBalanceCash(expectedBalanceCash);

        assertEquals(expectedId, accountBalance.getId());
        assertEquals(expectedAccountId, accountBalance.getAccountId());
        assertEquals(expectedBalanceFood, accountBalance.getBalanceFood());
        assertEquals(expectedBalanceMeal, accountBalance.getBalanceMeal());
        assertEquals(expectedBalanceCash, accountBalance.getBalanceCash());
    }
}
