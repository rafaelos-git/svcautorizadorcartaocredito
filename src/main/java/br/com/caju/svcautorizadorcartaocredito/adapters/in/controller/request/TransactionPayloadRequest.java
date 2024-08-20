package br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransactionPayloadRequest {
    @NotBlank(message = "A transacao deve ter um numero de conta")
    private String account;
    @NotNull(message = "O valor da transacao deve ser fornecido")
    @Positive(message = "O valor da transacao deve ser positivo")
    private Double totalAmount;
    @NotBlank(message = "A transacao deve ter um mcc")
    private String mcc;
    @NotBlank(message = "A transacao deve ter um nome de estabelecimento")
    private String merchant;
}
