package com.leultewolde.hidmo.kmingredientsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.service.PreparedFoodService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreparedFoodController.class)
class PreparedFoodControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private PreparedFoodService service;

    @Test
    void shouldReturnPreparedFoods() throws Exception {
        PreparedFoodResponseDTO responseDTO = new PreparedFoodResponseDTO();
        responseDTO.setName("Sauce");

        when(service.getAllPreparedFoods()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/v1/ingredients/prepared-foods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sauce"));
    }

    @Test
    void shouldCreatePreparedFood() throws Exception {
        PreparedFoodResponseDTO dto = new PreparedFoodResponseDTO();
        dto.setName("Chili");

        when(service.createPreparedFood(any())).thenReturn(dto);

        UUID usageUUID = UUID.randomUUID();

        String json = """
            {
              "name": "Chili",
              "quantity": 1,
              "unit": "bowl",
              "datePrepared": "2025-06-15",
              "expiryDate": "2025-06-18",
              "storageLocation": "fridge",
              "ingredientsUsed": [
                {"ingredientId": "%s", "quantity": 1}
              ]
            }
        """.formatted(usageUUID);

        mockMvc.perform(post("/v1/ingredients/prepared-foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Chili"));
    }

    @Test
    void shouldFailValidationWhenNameMissing() throws Exception {
        UUID usageUUID = UUID.randomUUID();

        String invalidJson = """
            {
              "quantity": 1,
              "unit": "bowl",
              "datePrepared": "2025-06-15",
              "expiryDate": "2025-06-18",
              "storageLocation": "fridge",
              "ingredientsUsed": [
                {"ingredientId": "%s", "quantity": 1}
              ]
            }
        """.formatted(usageUUID);

        mockMvc.perform(post("/v1/ingredients/prepared-foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
