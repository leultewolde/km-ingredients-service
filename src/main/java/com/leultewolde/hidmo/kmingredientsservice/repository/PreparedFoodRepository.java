package com.leultewolde.hidmo.kmingredientsservice.repository;

import com.leultewolde.hidmo.kmingredientsservice.model.PreparedFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PreparedFoodRepository extends JpaRepository<PreparedFood, UUID> {
}
