package com.leultewolde.hidmo.kmingredientsservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Entity
@EqualsAndHashCode(callSuper = true)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PreparedFood extends Ingredient {
    private LocalDate datePrepared;
    private String storageLocation;

    @Enumerated(EnumType.STRING)
    private PreparedFoodStatus preparedStatus;

    @OneToMany(cascade = CascadeType.ALL)
    private List<IngredientUsage> ingredientsUsed;
}

