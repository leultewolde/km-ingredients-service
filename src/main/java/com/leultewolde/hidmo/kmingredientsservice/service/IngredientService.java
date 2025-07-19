package com.leultewolde.hidmo.kmingredientsservice.service;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
import com.leultewolde.hidmo.kmingredientsservice.mapper.IngredientMapper;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.leultewolde.hidmo.kmingredientsservice.constant.WebSocketTopics;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository repo;
    private final IngredientMapper mapper;
    private final SimpMessagingTemplate ws;

    public List<IngredientResponseDTO> getAll(Pageable pageable) {
        return repo.findAll(pageable).stream().map(mapper::toDTO).toList();
    }

    public IngredientResponseDTO getById(UUID id) {
        return repo.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", id));
    }

    @Transactional
    public IngredientResponseDTO create(IngredientRequestDTO dto) {
        if (dto.getBarcode() != null) {
            var existingOpt = repo.findByBarcode(dto.getBarcode());
            if (existingOpt.isPresent()) {
                Ingredient existing = existingOpt.get();
                existing.setQuantity(existing.getQuantity().add(dto.getQuantity()));
                existing.setStatus(dto.getStatus() != null ? dto.getStatus() : existing.getStatus());
                IngredientResponseDTO saved = mapper.toDTO(repo.save(existing));
                ws.convertAndSend(WebSocketTopics.INGREDIENTS, getAll(PageRequest.of(0, 20)));
                return saved;
            }
        }

        Ingredient ing = mapper.toEntity(dto);
        if (ing.getStatus() == null) {
            ing.setStatus(IngredientStatus.AVAILABLE);
        }
        IngredientResponseDTO saved = mapper.toDTO(repo.save(ing));
        ws.convertAndSend(WebSocketTopics.INGREDIENTS, getAll(PageRequest.of(0, 20)));
        return saved;
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
        ws.convertAndSend(WebSocketTopics.INGREDIENTS, getAll(PageRequest.of(0, 20)));
    }
}
