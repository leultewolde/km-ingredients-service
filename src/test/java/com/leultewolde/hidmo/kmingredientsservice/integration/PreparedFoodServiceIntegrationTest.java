package com.leultewolde.hidmo.kmingredientsservice.integration;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import com.leultewolde.hidmo.kmingredientsservice.service.PreparedFoodService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PreparedFoodServiceIntegrationTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private PreparedFoodService preparedFoodService;

    @Test
    void shouldCreatePreparedFoodWithIngredient() {
        UUID ingId = ingredientService.create(new IngredientRequestDTO(
                "Tomato", new BigDecimal("3.0"), "pcs",
                new BigDecimal("1.50"), LocalDate.now(),
                LocalDate.now().plusDays(5), "Pantry",
                IngredientStatus.AVAILABLE, "TOM-001", null
        )).getId();

        IngredientUsageRequestDTO usage = new IngredientUsageRequestDTO(ingId, new BigDecimal("2.0"));

        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
                "Tomato Sauce", new BigDecimal("1.0"), "bottle",
                LocalDate.now(), LocalDate.now().plusDays(10), "Fridge",
                IngredientStatus.AVAILABLE, null, List.of(usage)
        );

        PreparedFoodResponseDTO result = preparedFoodService.createPreparedFood(dto);

        assertEquals("Tomato Sauce", result.getName());
        assertEquals(1, result.getIngredientsUsed().size());
        assertEquals(ingId, result.getIngredientsUsed().getFirst().getIngredientId());
    }

    @Test
    void shouldThrowIfIngredientMissing() {
        IngredientUsageRequestDTO invalidUsage = new IngredientUsageRequestDTO(UUID.randomUUID(), BigDecimal.ONE);
        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
                "Invalid Food", BigDecimal.ONE, "bowl",
                LocalDate.now(), LocalDate.now().plusDays(1), "Freezer",
                IngredientStatus.AVAILABLE, null, List.of(invalidUsage)
        );
        assertThrows(RuntimeException.class, () -> preparedFoodService.createPreparedFood(dto));
    }
}
