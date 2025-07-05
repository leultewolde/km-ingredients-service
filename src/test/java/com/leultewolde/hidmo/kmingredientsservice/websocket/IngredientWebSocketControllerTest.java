package com.leultewolde.hidmo.kmingredientsservice.websocket;

import com.leultewolde.hidmo.kmingredientsservice.controller.IngredientWebSocketController;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import com.leultewolde.hidmo.kmingredientsservice.service.PreparedFoodService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientWebSocketControllerTest {

    @Mock
    private IngredientService ingredientService;
    @Mock
    private PreparedFoodService preparedFoodService;

    @InjectMocks
    private IngredientWebSocketController controller;

    @Test
    void streamIngredientsReturnsList() {
        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setName("Cheese");
        when(ingredientService.getAll(org.springframework.data.domain.PageRequest.of(0,20)))
                .thenReturn(List.of(dto));

        List<IngredientResponseDTO> result = controller.streamIngredients();
        assertEquals(1, result.size());
        assertEquals("Cheese", result.getFirst().getName());
    }

    @Test
    void streamPreparedFoodsReturnsList() {
        PreparedFoodResponseDTO dto = new PreparedFoodResponseDTO();
        dto.setName("Soup");
        when(preparedFoodService.getAllPreparedFoods(org.springframework.data.domain.PageRequest.of(0,20)))
                .thenReturn(List.of(dto));

        List<PreparedFoodResponseDTO> result = controller.streamPreparedFoods();
        assertEquals(1, result.size());
        assertEquals("Soup", result.getFirst().getName());
    }
}
