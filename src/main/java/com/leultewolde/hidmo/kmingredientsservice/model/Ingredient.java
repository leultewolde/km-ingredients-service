package com.leultewolde.hidmo.kmingredientsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal price;
    private LocalDate dateBought;
    private LocalDate expiryDate;
    private String location;

    @Enumerated(EnumType.STRING)
    private IngredientStatus status;

    private String barcode;
    private String imagePath;
}

