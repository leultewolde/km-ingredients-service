package com.leultewolde.hidmo.kmingredientsservice.mapper.context.impl;

import com.leultewolde.hidmo.kmingredientsservice.mapper.context.IngredientResolver;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.repository.IngredientRepository;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IngredientResolverImplTest {

    private IngredientRepository repository;
    private IngredientResolver resolver;

    @BeforeEach
    void setUp() {
        repository = mock(IngredientRepository.class);
        resolver = new IngredientResolverImpl(repository);
    }

    @Test
    void resolve_existingIngredient_returnsEntity() {
        UUID id = UUID.randomUUID();
        Ingredient ingredient = new Ingredient();
        ingredient.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(ingredient));

        Ingredient result = resolver.resolve(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(repository).findById(id);
    }

    @Test
    void resolve_nonexistentIngredient_throwsException() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            resolver.resolve(id);
        });

        assertTrue(ex.getMessage().contains("Ingredient"));
        verify(repository).findById(id);
    }
}
