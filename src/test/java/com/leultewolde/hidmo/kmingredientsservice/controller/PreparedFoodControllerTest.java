package com.leultewolde.hidmo.kmingredientsservice.controller;

import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreparedFoodController.class)
class PreparedFoodControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private PreparedFoodService service;

    @Test
    void shouldReturnPreparedFoods() throws Exception {
        PreparedFoodResponseDTO responseDTO = new PreparedFoodResponseDTO();
        responseDTO.setName("Sauce");

        when(service.getAllPreparedFoods(any(org.springframework.data.domain.Pageable.class))).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/v1/prepared-foods"))
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

        mockMvc.perform(post("/v1/prepared-foods")
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

        mockMvc.perform(post("/v1/prepared-foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnIngredientById() throws Exception {
        UUID id = UUID.randomUUID();
        PreparedFoodResponseDTO dto = new PreparedFoodResponseDTO();
        dto.setId(id);
        dto.setName("Leftovers");

        when(service.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/v1/prepared-foods/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Leftovers"));
    }

    @Test
    void shouldReturnNotFoundWhenIngredientByIdNotExists() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getById(id)).thenThrow(new ResourceNotFoundException("PreparedFood","id",id));

        mockMvc.perform(get("/v1/prepared-foods/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeletePreparedFood() throws Exception {
        UUID id = UUID.randomUUID();
        org.mockito.Mockito.doNothing().when(service).deletePreparedFood(id);

        mockMvc.perform(delete("/v1/prepared-foods/" + id))
                .andExpect(status().isNoContent());
    }
}
