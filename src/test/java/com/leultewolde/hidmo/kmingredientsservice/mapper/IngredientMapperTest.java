package com.leultewolde.hidmo.kmingredientsservice.mapper;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IngredientMapperTest {

    private final IngredientMapper mapper = Mappers.getMapper(IngredientMapper.class);

    @Test
    void testToDto() {
        Ingredient ingredient = new Ingredient();
        UUID id = UUID.randomUUID();
        ingredient.setId(id);
        ingredient.setName("Butter");
        ingredient.setQuantity(BigDecimal.ONE);
        ingredient.setUnit("kg");
        ingredient.setPrice(new BigDecimal("4.50"));
        ingredient.setDateBought(LocalDate.of(2024, 1, 1));
        ingredient.setExpiryDate(LocalDate.of(2024, 6, 1));
        ingredient.setLocation("Fridge");
        ingredient.setStatus(IngredientStatus.AVAILABLE);
        ingredient.setBarcode("1234567890");
        ingredient.setImagePath("/images/butter.png");

        IngredientResponseDTO dto = mapper.toDTO(ingredient);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Butter", dto.getName());
        assertEquals(BigDecimal.ONE, dto.getQuantity());
        assertEquals("kg", dto.getUnit());
        assertEquals(new BigDecimal("4.50"), dto.getPrice());
        assertEquals(LocalDate.of(2024, 1, 1), dto.getDateBought());
        assertEquals(LocalDate.of(2024, 6, 1), dto.getExpiryDate());
        assertEquals("Fridge", dto.getLocation());
        assertEquals(IngredientStatus.AVAILABLE, dto.getStatus());
        assertEquals("1234567890", dto.getBarcode());
        assertEquals("/images/butter.png", dto.getImagePath());
    }

    @Test
    void testToEntity() {
        IngredientRequestDTO dto = new IngredientRequestDTO();
        dto.setName("Milk");
        dto.setQuantity(new BigDecimal("2.0"));
        dto.setUnit("liters");
        dto.setPrice(new BigDecimal("3.75"));
        dto.setDateBought(LocalDate.of(2024, 2, 10));
        dto.setExpiryDate(LocalDate.of(2024, 3, 1));
        dto.setLocation("Pantry");
        dto.setStatus(IngredientStatus.OUT_OF_STOCK);
        dto.setBarcode("9876543210");
        dto.setImagePath("/images/milk.png");

        Ingredient entity = mapper.toEntity(dto);

        assertNotNull(entity);

        assertNull(entity.getId());
        assertEquals("Milk", entity.getName());
        assertEquals(new BigDecimal("2.0"), entity.getQuantity());
        assertEquals("liters", entity.getUnit());
        assertEquals(new BigDecimal("3.75"), entity.getPrice());
        assertEquals(LocalDate.of(2024, 2, 10), entity.getDateBought());
        assertEquals(LocalDate.of(2024, 3, 1), entity.getExpiryDate());
        assertEquals("Pantry", entity.getLocation());
        assertEquals(IngredientStatus.OUT_OF_STOCK, entity.getStatus());
        assertEquals("9876543210", entity.getBarcode());
        assertEquals("/images/milk.png", entity.getImagePath());
    }

    @Test
    void testNullToDto() {
        assertNull(mapper.toDTO(null));
    }

    @Test
    void testNullToEntity() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void testToEntityWithNullStatus() {
        IngredientRequestDTO dto = new IngredientRequestDTO();
        dto.setName("Sugar");
        dto.setQuantity(new BigDecimal("1.5"));
        dto.setStatus(null);

        Ingredient entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals("Sugar", entity.getName());
        assertEquals(new BigDecimal("1.5"), entity.getQuantity());
        assertNull(entity.getStatus());
    }
}
