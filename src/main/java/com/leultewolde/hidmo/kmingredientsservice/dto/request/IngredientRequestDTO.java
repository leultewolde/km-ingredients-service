package com.leultewolde.hidmo.kmingredientsservice.dto.request;

import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quantity must be positive")
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or more")
    private BigDecimal price;

    @NotNull(message = "Date bought is required")
    private LocalDate dateBought;

    @NotNull(message = "Date expired is required")
    private LocalDate expiryDate;

    private String location;

    private IngredientStatus status = IngredientStatus.AVAILABLE;

    private String barcode;

    private String imagePath;
}

