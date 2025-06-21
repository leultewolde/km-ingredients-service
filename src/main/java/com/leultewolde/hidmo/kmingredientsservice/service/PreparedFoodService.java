package com.leultewolde.hidmo.kmingredientsservice.service;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
import com.leultewolde.hidmo.kmingredientsservice.mapper.IngredientUsageMapper;
import com.leultewolde.hidmo.kmingredientsservice.mapper.PreparedFoodMapper;
import com.leultewolde.hidmo.kmingredientsservice.mapper.context.IngredientResolver;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientUsage;
import com.leultewolde.hidmo.kmingredientsservice.model.PreparedFood;
import com.leultewolde.hidmo.kmingredientsservice.model.PreparedFoodStatus;
import com.leultewolde.hidmo.kmingredientsservice.repository.IngredientRepository;
import com.leultewolde.hidmo.kmingredientsservice.repository.PreparedFoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreparedFoodService {

    private final PreparedFoodRepository preparedFoodRepo;
    private final IngredientRepository ingredientRepo;
    private final PreparedFoodMapper mapper;
    private final IngredientUsageMapper ingredientUsageMapper;
    private final IngredientResolver resolver;

    @Transactional
    public PreparedFoodResponseDTO createPreparedFood(PreparedFoodRequestDTO dto) {
        PreparedFood food = mapper.toEntity(dto);
        if (dto.getStatus() == null) {
            food.setStatus(IngredientStatus.AVAILABLE);
        } else {
            food.setStatus(dto.getStatus());
        }

        if (dto.getPreparedStatus() == null) {
            food.setPreparedStatus(PreparedFoodStatus.STORED);
        } else {
            food.setPreparedStatus(dto.getPreparedStatus());
        }

        List<IngredientUsage> usageList = dto.getIngredientsUsed().stream().map(usageDTO -> {
            IngredientUsage usage = ingredientUsageMapper.toEntity(usageDTO, resolver);
            ingredientRepo.findById(usageDTO.getIngredientId())
                    .ifPresentOrElse(usage::setIngredient,
                            () -> { throw new ResourceNotFoundException("Ingredient", "id", usageDTO.getIngredientId()); });
            return usage;
        }).toList();

        food.setIngredientsUsed(usageList);
        return mapper.toDTO(preparedFoodRepo.save(food));
    }

    @Transactional(readOnly = true)
    public List<PreparedFoodResponseDTO> getAllPreparedFoods() {
        return preparedFoodRepo.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }
}

