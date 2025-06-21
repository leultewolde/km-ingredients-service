package com.leultewolde.hidmo.kmingredientsservice.dto.response;

import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IngredientResponseDTO {
    private UUID id;
    private String name;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal price;
    private LocalDate dateBought;
    private LocalDate expiryDate;
    private String location;
    private IngredientStatus status;
    private String barcode;
    private String imagePath;
}
