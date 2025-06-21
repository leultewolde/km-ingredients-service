package com.leultewolde.hidmo.kmingredientsservice.controller;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService service;

    @GetMapping
    public ResponseEntity<List<IngredientResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponseDTO> getById(@PathVariable UUID id) {
        IngredientResponseDTO dto = service.getById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-barcode/{barcode}")
    public ResponseEntity<IngredientResponseDTO> getByBarcode(@PathVariable String barcode) {
        IngredientResponseDTO dto = service.getByBarcode(barcode);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody @Valid IngredientRequestDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto).getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

