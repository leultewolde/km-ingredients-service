package com.leultewolde.hidmo.kmingredientsservice.component;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IngredientComponentTest {

    @Autowired
    private IngredientService service;

    @Test
    void canCreateAndRetrieveIngredient() {
        IngredientRequestDTO dto = new IngredientRequestDTO(
            "Butter", new BigDecimal("2.0"), "kg",
            new BigDecimal("8.50"), LocalDate.now(),
            LocalDate.now().plusDays(60), "Fridge",
            IngredientStatus.AVAILABLE, "BUT123", "/img/butter.jpg"
        );

        UUID id = service.create(dto).getId();
        assertNotNull(id);

        IngredientResponseDTO retrieved = service.getById(id);
        assertEquals("Butter", retrieved.getName());
        assertEquals("kg", retrieved.getUnit());
    }

    @Test
    void shouldThrowWhenBarcodeNotFound() {
        assertThrows(RuntimeException.class, () -> service.getByBarcode("nonexistent"));
    }

    @Test
    void shouldDeleteIngredient() {
        IngredientRequestDTO dto = new IngredientRequestDTO(
            "Yogurt", new BigDecimal("1.0"), "L",
            new BigDecimal("3.50"), LocalDate.now(),
            LocalDate.now().plusDays(7), "Shelf",
            IngredientStatus.AVAILABLE, "YOG111", "/img/yogurt.jpg"
        );

        UUID id = service.create(dto).getId();
        assertDoesNotThrow(() -> service.getById(id));

        service.delete(id);

        assertThrows(RuntimeException.class, () -> service.getById(id));
    }
}
