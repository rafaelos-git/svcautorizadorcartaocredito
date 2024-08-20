package br.com.caju.svcautorizadorcartaocredito.adapters.out.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "accounts")
public class AccountBalanceEntity {
    @Id
    private String _id;
    private String accountId;
    private double balanceFood;
    private double balanceMeal;
    private double balanceCash;
}
