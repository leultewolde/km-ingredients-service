package com.leultewolde.hidmo.kmingredientsservice.mapper;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientUsageResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PreparedFoodMapperTest {

    @Autowired
    private PreparedFoodMapper mapper;

    @Test
    void testToDto() {
        UUID id = UUID.randomUUID();
        Ingredient baseIngredient = new Ingredient();
        baseIngredient.setId(id);
        baseIngredient.setName("Tomato Sauce");
        baseIngredient.setQuantity(new BigDecimal("2.0"));
        baseIngredient.setUnit("liters");
        baseIngredient.setStatus(IngredientStatus.AVAILABLE);
        baseIngredient.setExpiryDate(LocalDate.of(2024, 12, 31));

        IngredientUsage usage = new IngredientUsage();
        usage.setId(UUID.randomUUID());
        usage.setIngredient(baseIngredient);
        usage.setQuantity(new BigDecimal("1.0"));

        PreparedFood food = new PreparedFood();
        food.setId(id);
        food.setName("Pizza Sauce");
        food.setQuantity(new BigDecimal("1.5"));
        food.setUnit("liters");
        food.setStatus(IngredientStatus.AVAILABLE);
        food.setExpiryDate(LocalDate.of(2024, 11, 30));
        food.setDatePrepared(LocalDate.of(2024, 10, 1));
        food.setStorageLocation("Fridge Top");
        food.setIngredientsUsed(List.of(usage));

        PreparedFoodResponseDTO dto = mapper.toDTO(food);

        assertNotNull(dto);
        assertEquals("Pizza Sauce", dto.getName());
        assertEquals(new BigDecimal("1.5"), dto.getQuantity());
        assertEquals("liters", dto.getUnit());
        assertEquals(LocalDate.of(2024, 11, 30), dto.getExpiryDate());
        assertEquals(LocalDate.of(2024, 10, 1), dto.getDatePrepared());
        assertEquals("Fridge Top", dto.getStorageLocation());
        assertEquals(IngredientStatus.AVAILABLE, dto.getStatus());

        assertNotNull(dto.getIngredientsUsed());
        assertEquals(1, dto.getIngredientsUsed().size());
        IngredientUsageResponseDTO usageDTO = dto.getIngredientsUsed().getFirst();
        assertEquals(id, usageDTO.getIngredientId());
        assertEquals(new BigDecimal("1.0"), usageDTO.getQuantity());
    }

    @Test
    void testToEntity() {
        UUID ingredientId = UUID.randomUUID();

        PreparedFoodRequestDTO dto = getPreparedFoodRequestDTO(ingredientId);

        PreparedFood entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Soup Base", entity.getName());
        assertEquals(new BigDecimal("2.5"), entity.getQuantity());
        assertEquals("liters", entity.getUnit());
        assertEquals(LocalDate.of(2024, 9, 15), entity.getDatePrepared());
        assertEquals(LocalDate.of(2024, 10, 1), entity.getExpiryDate());
        assertEquals("Freezer Middle", entity.getStorageLocation());
        assertEquals(IngredientStatus.AVAILABLE, entity.getStatus());

        assertNotNull(entity.getIngredientsUsed());
        assertEquals(1, entity.getIngredientsUsed().size());

        IngredientUsage usage = entity.getIngredientsUsed().getFirst();
        assertEquals(new BigDecimal("0.75"), usage.getQuantity());
        assertNull(usage.getIngredient()); // since ingredientId is not mapped into Ingredient
    }

    @NotNull
    private static PreparedFoodRequestDTO getPreparedFoodRequestDTO(UUID ingredientId) {
        IngredientUsageRequestDTO usageDTO = new IngredientUsageRequestDTO();
        usageDTO.setIngredientId(ingredientId);
        usageDTO.setQuantity(new BigDecimal("0.75"));

        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO();
        dto.setName("Soup Base");
        dto.setQuantity(new BigDecimal("2.5"));
        dto.setUnit("liters");
        dto.setDatePrepared(LocalDate.of(2024, 9, 15));
        dto.setExpiryDate(LocalDate.of(2024, 10, 1));
        dto.setStorageLocation("Freezer Middle");
        dto.setStatus(IngredientStatus.AVAILABLE);
        dto.setIngredientsUsed(Collections.singletonList(usageDTO));
        return dto;
    }

    @Test
    void testNullToDto() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void testNullToEntity() {
        assertNull(mapper.toEntity(null));
    }
}
