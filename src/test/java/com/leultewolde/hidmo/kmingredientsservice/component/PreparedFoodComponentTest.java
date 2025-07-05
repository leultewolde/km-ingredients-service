package com.leultewolde.hidmo.kmingredientsservice.component;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import com.leultewolde.hidmo.kmingredientsservice.service.PreparedFoodService;
import org.junit.jupiter.api.Test;
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
class PreparedFoodComponentTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private PreparedFoodService preparedFoodService;

    @Test
    void canCreatePreparedFoodWithIngredient() {
        IngredientRequestDTO ingDto = new IngredientRequestDTO(
            "Tomato", new BigDecimal("3.0"), "pcs",
            new BigDecimal("1.50"), LocalDate.now(),
            LocalDate.now().plusDays(5), "Pantry",
            IngredientStatus.AVAILABLE, "TOM123", "/img/tomato.jpg"
        );

        UUID ingId = ingredientService.create(ingDto).getId();

        IngredientUsageRequestDTO usageDto = new IngredientUsageRequestDTO(ingId, new BigDecimal("2.0"));

        PreparedFoodRequestDTO prepDto = new PreparedFoodRequestDTO(
            "Tomato Sauce", new BigDecimal("1.0"), "bottle",
            LocalDate.now(), LocalDate.now().plusDays(10), "Fridge",
            IngredientStatus.AVAILABLE, null, List.of(usageDto)
        );

        PreparedFoodResponseDTO response = preparedFoodService.createPreparedFood(prepDto);

        assertEquals("Tomato Sauce", response.getName());
        assertEquals(1, response.getIngredientsUsed().size());
        assertEquals(ingId, response.getIngredientsUsed().getFirst().getIngredientId());
    }

    @Test
    void shouldFailToCreatePreparedFoodIfIngredientMissing() {
        IngredientUsageRequestDTO usage = new IngredientUsageRequestDTO(UUID.randomUUID(), new BigDecimal("1"));

        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
            "Invalid Mix", new BigDecimal("0.5"), "bowl",
            LocalDate.now(), LocalDate.now().plusDays(1), "Freezer",
            IngredientStatus.AVAILABLE, null, List.of(usage)
        );

        assertThrows(RuntimeException.class, () -> preparedFoodService.createPreparedFood(dto));
    }

    @Test
    void shouldListAllPreparedFoods() {
        List<PreparedFoodResponseDTO> all = preparedFoodService.getAllPreparedFoods(org.springframework.data.domain.PageRequest.of(0, 20));
        assertNotNull(all);
    }
}
