package br.com.caju.svcautorizadorcartaocredito.application.core.domain;

public class AccountBalance {
    private String id;
    private String accountId;
    private double balanceFood;
    private double balanceMeal;
    private double balanceCash;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getBalanceFood() {
        return balanceFood;
    }

    public void setBalanceFood(double balanceFood) {
        this.balanceFood = balanceFood;
    }

    public double getBalanceMeal() {
        return balanceMeal;
    }

    public void setBalanceMeal(double balanceMeal) {
        this.balanceMeal = balanceMeal;
    }

    public double getBalanceCash() {
        return balanceCash;
    }

    public void setBalanceCash(double balanceCash) {
        this.balanceCash = balanceCash;
    }
}
