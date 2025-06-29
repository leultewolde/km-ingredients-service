package com.leultewolde.hidmo.kmingredientsservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ExceptionHandlingIntegrationTest {

    @LocalServerPort
    int port;

    @Test
    void shouldReturn400ForInvalidIngredientPayload() {
        RestTemplate rest = new RestTemplate();
        String url = "http://localhost:" + port + "/v1/ingredients";
        String invalidJson = "{}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(invalidJson, headers);

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.BadRequest.class, () ->
                rest.exchange(url, HttpMethod.POST, request, String.class)
        );

        assertTrue(ex.getResponseBodyAsString().contains("\"name\":\"Name is required\""));
    }

    @Test
    void shouldReturn404ForMissingIngredient() {
        RestTemplate rest = new RestTemplate();
        String url = "http://localhost:" + port + "/v1/ingredients/" + UUID.randomUUID();

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.NotFound.class, () ->
                rest.getForEntity(url, String.class)
        );

        assertTrue(ex.getMessage().contains("404"));
        assertTrue(ex.getResponseBodyAsString().contains("Ingredient not found"));
    }
}

