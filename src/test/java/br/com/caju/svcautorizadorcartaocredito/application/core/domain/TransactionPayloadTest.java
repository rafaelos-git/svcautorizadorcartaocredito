package br.com.caju.svcautorizadorcartaocredito.application.core.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransactionPayloadTest {

    @Test
    public void testGettersAndSetters() {
        TransactionPayload transactionPayload = new TransactionPayload();
        String id = "123";
        String account = "account123";
        double totalAmount = 250.75;
        String mcc = "5411";
        String merchant = "Test Merchant";

        transactionPayload.setId(id);
        transactionPayload.setAccount(account);
        transactionPayload.setTotalAmount(totalAmount);
        transactionPayload.setMcc(mcc);
        transactionPayload.setMerchant(merchant);

        assertThat(transactionPayload.getId()).isEqualTo(id);
        assertThat(transactionPayload.getAccount()).isEqualTo(account);
        assertThat(transactionPayload.getTotalAmount()).isEqualTo(totalAmount);
        assertThat(transactionPayload.getMcc()).isEqualTo(mcc);
        assertThat(transactionPayload.getMerchant()).isEqualTo(merchant);
    }
}
