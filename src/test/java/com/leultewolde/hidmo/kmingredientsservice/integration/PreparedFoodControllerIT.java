package com.leultewolde.hidmo.kmingredientsservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import com.leultewolde.hidmo.kmingredientsservice.repository.PreparedFoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PreparedFoodControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private IngredientService ingredientService;
    @Autowired private PreparedFoodRepository preparedFoodRepository;

    private UUID ingredientId;

    @BeforeEach
    void setup() {
        preparedFoodRepository.deleteAll();
        IngredientRequestDTO ing = new IngredientRequestDTO(
                "Chili", new BigDecimal("2.0"), "cups",
                new BigDecimal("1.00"), LocalDate.now(),
                LocalDate.now().plusDays(5), "Pantry",
                IngredientStatus.AVAILABLE, "CHILI-123", null
        );
        ingredientId = ingredientService.create(ing).getId();
    }

    @Test
    void shouldCreatePreparedFood() throws Exception {
        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
                "Chili Paste", new BigDecimal("1"), "jar",
                LocalDate.now(), LocalDate.now().plusDays(7),
                "Fridge", IngredientStatus.AVAILABLE, null,
                List.of(new IngredientUsageRequestDTO(ingredientId, BigDecimal.ONE))
        );

        mockMvc.perform(post("/v1/prepared-foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Chili Paste"));
    }

    @Test
    void shouldRejectMissingFields() throws Exception {
        mockMvc.perform(post("/v1/prepared-foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void shouldListPreparedFoods() throws Exception {
        mockMvc.perform(get("/v1/prepared-foods"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldDeletePreparedFood() throws Exception {
        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
                "Chili Paste", new BigDecimal("1"), "jar",
                LocalDate.now(), LocalDate.now().plusDays(1),
                "Fridge", IngredientStatus.AVAILABLE, null,
                List.of(new IngredientUsageRequestDTO(ingredientId, BigDecimal.ONE))
        );

        mockMvc.perform(post("/v1/prepared-foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        UUID foodId = preparedFoodRepository.findAll().getFirst().getId();

        mockMvc.perform(delete("/v1/prepared-foods/" + foodId))
                .andExpect(status().isNoContent());
    }

    @Test
    void createWithUnknownIngredientFails() throws Exception {
        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
                "Mystery", new BigDecimal("1"), "jar",
                LocalDate.now(), LocalDate.now().plusDays(1),
                "Freezer", IngredientStatus.AVAILABLE, null,
                List.of(new IngredientUsageRequestDTO(UUID.randomUUID(), BigDecimal.ONE))
        );

        mockMvc.perform(post("/v1/prepared-foods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("Ingredient")));
    }


    @Test
    void shouldAllowCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/v1/prepared-foods")
                        .header("Origin", "http://example.com")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().string("Access-Control-Allow-Methods", containsString("POST")))
                .andExpect(header().string("Access-Control-Allow-Headers", containsString("Content-Type")));
    }
}
