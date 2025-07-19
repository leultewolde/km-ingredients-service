package com.leultewolde.hidmo.kmingredientsservice.controller;

import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import com.leultewolde.hidmo.kmingredientsservice.service.PreparedFoodService;
import com.leultewolde.hidmo.kmingredientsservice.constant.WebSocketTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IngredientWebSocketController {

    private final IngredientService ingredientService;
    private final PreparedFoodService preparedFoodService;

    @MessageMapping("/ingredients")
    @SendTo(WebSocketTopics.INGREDIENTS)
    public List<IngredientResponseDTO> streamIngredients() {
        return ingredientService.getAll(PageRequest.of(0, 20));
    }

    @MessageMapping("/prepared-foods")
    @SendTo(WebSocketTopics.PREPARED_FOODS)
    public List<PreparedFoodResponseDTO> streamPreparedFoods() {
        return preparedFoodService.getAllPreparedFoods(PageRequest.of(0, 20));
    }
}
