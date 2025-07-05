package com.leultewolde.hidmo.kmingredientsservice.controller;

import com.leultewolde.hidmo.kmingredientsservice.service.PreparedFoodService;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/v1/prepared-foods")
@RequiredArgsConstructor
public class PreparedFoodController {

    private final PreparedFoodService preparedFoodService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid PreparedFoodRequestDTO dto) {
        PreparedFoodResponseDTO result = preparedFoodService.createPreparedFood(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result.getName());
    }

    @GetMapping
    public ResponseEntity<List<PreparedFoodResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(preparedFoodService.getAllPreparedFoods(PageRequest.of(page, size)));
    }
}

