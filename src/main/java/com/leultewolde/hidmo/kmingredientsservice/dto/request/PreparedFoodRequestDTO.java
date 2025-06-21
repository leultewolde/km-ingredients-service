package com.leultewolde.hidmo.kmingredientsservice.dto.request;

import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.model.PreparedFoodStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreparedFoodRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Date prepared is required")
    private LocalDate datePrepared;

    private LocalDate expiryDate;

    private String storageLocation;

    private IngredientStatus status = IngredientStatus.AVAILABLE;
    private PreparedFoodStatus preparedStatus = PreparedFoodStatus.STORED;

    @NotEmpty(message = "Ingredients used cannot be empty")
    @Valid
    private List<IngredientUsageRequestDTO> ingredientsUsed;
}

