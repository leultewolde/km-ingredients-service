package com.leultewolde.hidmo.kmingredientsservice.mapper.context;

import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;

import java.util.UUID;

public interface IngredientResolver {
    Ingredient resolve(UUID id);
}
