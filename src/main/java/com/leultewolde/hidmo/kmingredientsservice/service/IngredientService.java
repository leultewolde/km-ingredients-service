package com.leultewolde.hidmo.kmingredientsservice.service;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
import com.leultewolde.hidmo.kmingredientsservice.mapper.IngredientMapper;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository repo;
    private final IngredientMapper mapper;

    public List<IngredientResponseDTO> getAll() {
        return repo.findAll().stream().map(mapper::toDTO).toList();
    }

    public IngredientResponseDTO getById(UUID id) {
        return repo.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
    }

    @Transactional
    public IngredientResponseDTO create(IngredientRequestDTO dto) {
        Ingredient ing = mapper.toEntity(dto);
        if (ing.getStatus() == null) {
            ing.setStatus(IngredientStatus.AVAILABLE);
        }
        return mapper.toDTO(repo.save(ing));
    }

    public IngredientResponseDTO getByBarcode(String barcode) {
        return repo.findByBarcode(barcode).map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "barcode", barcode));
    }

    @Transactional
    public void delete(UUID id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient", "id", id);
        }
        repo.deleteById(id);
    }
}
