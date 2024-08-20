package br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionPayloadRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidTransactionPayloadRequest() {
        TransactionPayloadRequest request = new TransactionPayloadRequest();
        request.setAccount("123456");
        request.setTotalAmount(100.0);
        request.setMcc("5411");
        request.setMerchant("Supermarket");

        Set<ConstraintViolation<TransactionPayloadRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação");
    }

    @Test
    public void testInvalidTransactionPayloadRequest() {
        TransactionPayloadRequest request = new TransactionPayloadRequest();
        request.setAccount("");
        request.setTotalAmount(-10.0);
        request.setMcc(null);
        request.setMerchant("");

        Set<ConstraintViolation<TransactionPayloadRequest>> violations = validator.validate(request);
        assertEquals(4, violations.size(), "Deve haver 4 violações de validação");

        for (ConstraintViolation<TransactionPayloadRequest> violation : violations) {
            String message = violation.getMessage();
            assertTrue(message.equals("A transacao deve ter um numero de conta") ||
                            message.equals("O valor da transacao deve ser positivo") ||
                            message.equals("A transacao deve ter um mcc") ||
                            message.equals("A transacao deve ter um nome de estabelecimento"),
                    "Mensagem de violação inesperada: " + message);
        }
    }

    @Test
    public void testGettersAndSetters() {
        TransactionPayloadRequest request = new TransactionPayloadRequest();
        request.setAccount("123456");
        request.setTotalAmount(100.0);
        request.setMcc("5411");
        request.setMerchant("Supermarket");

        assertEquals("123456", request.getAccount());
        assertEquals(100.0, request.getTotalAmount());
        assertEquals("5411", request.getMcc());
        assertEquals("Supermarket", request.getMerchant());
    }
}

