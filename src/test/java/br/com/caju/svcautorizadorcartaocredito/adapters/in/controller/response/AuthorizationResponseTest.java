package br.com.caju.svcautorizadorcartaocredito.adapters.in.controller.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AuthorizationResponseTest {

    @Test
    public void testDefaultConstructor() {
        AuthorizationResponse response = new AuthorizationResponse();

        assertNull(response.getCode());
    }

    @Test
    public void testAllArgsConstructor() {
        String code = "00";
        AuthorizationResponse response = new AuthorizationResponse(code);

        assertEquals(code, response.getCode());
    }

    @Test
    public void testSetterAndGetter() {
        AuthorizationResponse response = new AuthorizationResponse();
        String code = "51";

        response.setCode(code);

        assertEquals(code, response.getCode());
    }
}
