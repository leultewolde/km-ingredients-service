package com.leultewolde.hidmo.kmingredientsservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IngredientControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private IngredientRequestDTO sampleDTO() {
        return new IngredientRequestDTO(
                "Test Ingredient", BigDecimal.valueOf(1.5), "kg",
                BigDecimal.valueOf(3.99), LocalDate.now(),
                LocalDate.now().plusDays(10), "Pantry",
                IngredientStatus.AVAILABLE, "SAMPLE-001", null
        );
    }

    @Test
    void shouldCreateAndRetrieveIngredient() throws Exception {
        String json = objectMapper.writeValueAsString(sampleDTO());

        String location = mockMvc.perform(post("/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/v1/ingredients/" + location.replace("\"", "")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Ingredient")));
    }

    @Test
    void shouldValidateMissingFields() throws Exception {
        mockMvc.perform(post("/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void shouldReturnNotFoundForInvalidId() throws Exception {
        mockMvc.perform(get("/v1/ingredients/123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteIngredient() throws Exception {
        String id = mockMvc.perform(post("/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDTO())))
                .andReturn().getResponse().getContentAsString().replace("\"", "");

        mockMvc.perform(delete("/v1/ingredients/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/ingredients/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUnknownIngredientReturns404() throws Exception {
        mockMvc.perform(delete("/v1/ingredients/123e4567-e89b-12d3-a456-426614174000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void getByBarcodeReturnsIngredient() throws Exception {
        IngredientRequestDTO dto = sampleDTO();
        String id = mockMvc.perform(post("/v1/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString().replace("\"", "");

        mockMvc.perform(get("/v1/ingredients/by-barcode/" + dto.getBarcode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)));
    }

    @Test
    void getByBarcodeNotFound() throws Exception {
        mockMvc.perform(get("/v1/ingredients/by-barcode/UNKNOWN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void shouldAllowCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/v1/ingredients")
                        .header("Origin", "http://example.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
    }
}
