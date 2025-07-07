package com.leultewolde.hidmo.kmingredientsservice.integration;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import com.leultewolde.hidmo.kmingredientsservice.service.PreparedFoodService;
import com.leultewolde.hidmo.kmingredientsservice.repository.PreparedFoodRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class PreparedFoodServiceIntegrationTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private PreparedFoodService preparedFoodService;

    @Autowired
    private PreparedFoodRepository preparedFoodRepository;

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
        assertEquals(
                new BigDecimal("1.0").stripTrailingZeros(),
                ingredientService.getById(ingId).getQuantity().stripTrailingZeros()
        );

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

    @Test
    void deletingPreparedFoodRestoresIngredientQuantities() {
        UUID ingId = ingredientService.create(new IngredientRequestDTO(
                "Onion", new BigDecimal("2"), "pcs",
                BigDecimal.ONE, LocalDate.now(),
                LocalDate.now().plusDays(3), "Pantry",
                IngredientStatus.AVAILABLE, "ON-1", null
        )).getId();

        IngredientUsageRequestDTO usage = new IngredientUsageRequestDTO(ingId, new BigDecimal("1"));
        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
                "Onion Mix", BigDecimal.ONE, "bowl",
                LocalDate.now(), LocalDate.now().plusDays(1), "Fridge",
                IngredientStatus.AVAILABLE, null, List.of(usage)
        );

        PreparedFoodResponseDTO created = preparedFoodService.createPreparedFood(dto);
        UUID foodId = created.getId();

        preparedFoodService.deletePreparedFood(foodId);

        assertEquals(0, ingredientService.getById(ingId).getQuantity().compareTo(new BigDecimal("2")));
    }
}
