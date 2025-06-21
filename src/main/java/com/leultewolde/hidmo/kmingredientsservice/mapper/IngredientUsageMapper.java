package com.leultewolde.hidmo.kmingredientsservice.mapper;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientUsageResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.mapper.context.IngredientResolver;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientUsage;
import org.mapstruct.*;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngredientUsageMapper {
    @Mapping(source = "ingredient.id", target = "ingredientId")
    IngredientUsageResponseDTO toDTO(IngredientUsage entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "ingredientId", target = "ingredient", qualifiedByName = "resolveIngredient")
    IngredientUsage toEntity(IngredientUsageRequestDTO dto, @Context IngredientResolver resolver);

    @Named("resolveIngredient")
    static Ingredient resolveIngredient(UUID id, @Context IngredientResolver resolver) {
        return id == null ? null : resolver.resolve(id);
    }
}
