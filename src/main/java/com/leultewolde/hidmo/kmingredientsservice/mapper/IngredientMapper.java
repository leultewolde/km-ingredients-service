package com.leultewolde.hidmo.kmingredientsservice.mapper;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IngredientMapper {

    IngredientResponseDTO toDTO(Ingredient ing);

    Ingredient toEntity(IngredientRequestDTO dto);
}
