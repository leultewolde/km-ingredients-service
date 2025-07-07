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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreparedFoodService {

    private final PreparedFoodRepository preparedFoodRepo;
    private final IngredientRepository ingredientRepo;
    private final PreparedFoodMapper mapper;
    private final IngredientUsageMapper ingredientUsageMapper;
    private final IngredientResolver resolver;
    private final SimpMessagingTemplate ws;

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
                    .ifPresentOrElse(ingredient -> {
                        usage.setIngredient(ingredient);
                        ingredient.setQuantity(ingredient.getQuantity().subtract(usageDTO.getQuantity()));
                        ingredientRepo.save(ingredient);
                    },
                            () -> { throw new ResourceNotFoundException("Ingredient", "id", usageDTO.getIngredientId()); });
            return usage;
        }).toList();

        food.setIngredientsUsed(usageList);
        PreparedFoodResponseDTO saved = mapper.toDTO(preparedFoodRepo.save(food));
        ws.convertAndSend("/topic/prepared-foods", fetchPreparedFoods(PageRequest.of(0, 20)));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<PreparedFoodResponseDTO> getAllPreparedFoods(Pageable pageable) {
        return fetchPreparedFoods(pageable);
    }

    @Transactional
    public void deletePreparedFood(UUID id) {
        PreparedFood food = preparedFoodRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PreparedFood", "id", id));

        for (IngredientUsage usage : food.getIngredientsUsed()) {
            var ing = ingredientRepo.findById(usage.getIngredient().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", usage.getIngredient().getId()));
            ing.setQuantity(ing.getQuantity().add(usage.getQuantity()));
            ingredientRepo.save(ing);
        }

        preparedFoodRepo.deleteById(id);
        ws.convertAndSend("/topic/prepared-foods", fetchPreparedFoods(PageRequest.of(0, 20)));
    }

    private List<PreparedFoodResponseDTO> fetchPreparedFoods(Pageable pageable) {
        return preparedFoodRepo.findAll(pageable).stream()
                .map(mapper::toDTO)
                .toList();
    }
}

