package com.leultewolde.hidmo.kmingredientsservice.mapper.context.impl;

import com.leultewolde.hidmo.kmingredientsservice.mapper.context.IngredientResolver;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IngredientResolverImpl implements IngredientResolver {

    private final IngredientRepository repository;

    @Override
    public Ingredient resolve(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found for ID: " + id));
    }
}
