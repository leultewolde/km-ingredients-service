package com.leultewolde.hidmo.kmingredientsservice.repository;

import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    Optional<Ingredient> findByName(String name);
    Optional<Ingredient> findByBarcode(String barcode);
}
