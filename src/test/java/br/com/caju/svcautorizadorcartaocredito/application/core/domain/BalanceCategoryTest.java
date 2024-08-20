package br.com.caju.svcautorizadorcartaocredito.application.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BalanceCategoryTest {

    @Test
    public void testEnumValues() {
        assertNotNull(BalanceCategory.FOOD);
        assertNotNull(BalanceCategory.MEAL);
        assertNotNull(BalanceCategory.CASH);
    }

    @Test
    public void testEnumValueOf() {
        assertEquals(BalanceCategory.FOOD, BalanceCategory.valueOf("FOOD"));
        assertEquals(BalanceCategory.MEAL, BalanceCategory.valueOf("MEAL"));
        assertEquals(BalanceCategory.CASH, BalanceCategory.valueOf("CASH"));
    }

    @Test
    public void testEnumOrdinal() {
        assertEquals(0, BalanceCategory.FOOD.ordinal());
        assertEquals(1, BalanceCategory.MEAL.ordinal());
        assertEquals(2, BalanceCategory.CASH.ordinal());
    }
}
