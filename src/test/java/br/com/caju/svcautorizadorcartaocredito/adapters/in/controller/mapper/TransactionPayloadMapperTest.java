package br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.mapper;

import br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.request.TransactionPayloadRequest;
import br.com.caju.svcautorizadorcartaocredito.application.core.domain.TransactionPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TransactionPayloadMapperTest {

    private TransactionPayloadMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(TransactionPayloadMapper.class);
    }

    @Test
    public void testToTransactionPayload() {
        TransactionPayloadRequest request = new TransactionPayloadRequest();
        request.setAccount("1234");
        request.setMcc("5811");
        request.setMerchant("Teste");
        request.setTotalAmount(100.0);

        TransactionPayload result = mapper.toTransactionPayload(request);

        assertEquals("1234", result.getAccount());
        assertEquals("5811", result.getMcc());
        assertEquals("Teste", result.getMerchant());
        assertEquals(100.0, result.getTotalAmount());
        assertNull(result.getId());
    }

    @Test
    public void testToTransactionPayloadWithNullRequest() {
        TransactionPayload result = mapper.toTransactionPayload(null);

        assertNull(result);
    }

    @Test
    public void testToTransactionPayloadWithNullTotalAmount() {
        TransactionPayloadRequest request = new TransactionPayloadRequest();
        request.setAccount("1234");
        request.setMcc("5811");
        request.setMerchant("Teste");
        request.setTotalAmount(null);

        TransactionPayload result = mapper.toTransactionPayload(request);

        assertEquals("1234", result.getAccount());
        assertEquals("5811", result.getMcc());
        assertEquals("Teste", result.getMerchant());
        assertEquals(0.0, result.getTotalAmount());
        assertNull(result.getId());
    }
}
