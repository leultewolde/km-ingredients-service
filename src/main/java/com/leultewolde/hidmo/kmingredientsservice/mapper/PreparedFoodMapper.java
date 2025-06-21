package com.leultewolde.hidmo.kmingredientsservice.mapper;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.PreparedFood;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = IngredientUsageMapper.class)
public interface PreparedFoodMapper {
    PreparedFoodResponseDTO toDTO(PreparedFood prep);
    PreparedFood toEntity(PreparedFoodRequestDTO dto);
}

