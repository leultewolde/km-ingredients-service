package com.leultewolde.hidmo.kmingredientsservice.mapper;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientUsageResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.mapper.context.IngredientResolver;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientUsage;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IngredientUsageMapperTest {

    private final IngredientUsageMapper mapper = Mappers.getMapper(IngredientUsageMapper.class);

    @Test
    void testToDTO() {
        UUID id = UUID.randomUUID();

        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);
        ingredient.setName("Salt");

        IngredientUsage usage = new IngredientUsage();
        usage.setIngredient(ingredient);
        usage.setQuantity(new BigDecimal("3.5"));

        IngredientUsageResponseDTO dto = mapper.toDTO(usage);

        assertNotNull(dto);
        assertEquals(id, dto.getIngredientId());
        assertEquals(new BigDecimal("3.5"), dto.getQuantity());
    }

    @Test
    void testToEntity_withResolvedIngredient() {
        UUID id = UUID.randomUUID();

        IngredientUsageRequestDTO dto = new IngredientUsageRequestDTO();
        dto.setIngredientId(id);
        dto.setQuantity(new BigDecimal("2.0"));

        Ingredient resolvedIngredient = new Ingredient();
        resolvedIngredient.setId(id);
        resolvedIngredient.setName("Flour");
        resolvedIngredient.setUnit("kg");

        // use a lambda for IngredientResolver (mock)
        IngredientResolver resolver = uuid -> {
            assertEquals(id, uuid); // sanity check
            return resolvedIngredient;
        };

        IngredientUsage entity = mapper.toEntity(dto, resolver);

        assertNotNull(entity);
        assertEquals(new BigDecimal("2.0"), entity.getQuantity());

        assertNotNull(entity.getIngredient());
        assertEquals(id, entity.getIngredient().getId());
        assertEquals("Flour", entity.getIngredient().getName());
        assertEquals("kg", entity.getIngredient().getUnit());
    }

    @Test
    void testToEntity_nullIngredientId() {
        IngredientUsageRequestDTO dto = new IngredientUsageRequestDTO();
        dto.setIngredientId(null);
        dto.setQuantity(new BigDecimal("1.0"));

        IngredientResolver resolver = id -> {
            fail("Resolver should not be called for null ID");
            return null;
        };

        IngredientUsage entity = mapper.toEntity(dto, resolver);

        assertNotNull(entity);
        assertEquals(new BigDecimal("1.0"), entity.getQuantity());
        assertNull(entity.getIngredient());
    }

    @Test
    void testToDTO_nullIngredient() {
        IngredientUsage usage = new IngredientUsage();
        usage.setIngredient(null);
        usage.setQuantity(new BigDecimal("5.0"));

        IngredientUsageResponseDTO dto = mapper.toDTO(usage);

        assertNotNull(dto);
        assertNull(dto.getIngredientId());
        assertEquals(new BigDecimal("5.0"), dto.getQuantity());
    }

    @Test
    void testNullInputs() {
        assertNull(mapper.toDTO(null));
        assertNull(mapper.toEntity(null, id -> fail("Should not call resolver on null input")));
    }
}
