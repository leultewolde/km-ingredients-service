package com.leultewolde.hidmo.kmingredientsservice.dto.response;

import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreparedFoodResponseDTO {
    private String name;
    private BigDecimal quantity;
    private String unit;
    private LocalDate datePrepared;
    private LocalDate expiryDate;
    private String storageLocation;
    private IngredientStatus status;
    private List<IngredientUsageResponseDTO> ingredientsUsed;
}

