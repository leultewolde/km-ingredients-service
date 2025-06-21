package com.leultewolde.hidmo.kmingredientsservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientUsageResponseDTO {
    private UUID ingredientId;
    private BigDecimal quantity;
}