package com.leultewolde.hidmo.kmingredientsservice.integration;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class IngredientServiceIntegrationTest {

    @Autowired
    private IngredientService service;

    private IngredientRequestDTO getSampleDTO() {
        return new IngredientRequestDTO(
                "Spinach", new BigDecimal("0.5"), "kg",
                new BigDecimal("1.99"), LocalDate.now(),
                LocalDate.now().plusDays(3), "Fridge",
                IngredientStatus.AVAILABLE, "BARCODE123", "/images/spinach.png"
        );
    }

    @Test
    void shouldCreateAndFetchIngredient() {
        IngredientRequestDTO dto = getSampleDTO();
        UUID id = service.create(dto).getId();

        IngredientResponseDTO result = service.getById(id);
        assertEquals("Spinach", result.getName());
        assertEquals("BARCODE123", result.getBarcode());
    }

    @Test
    void shouldDeleteIngredient() {
        UUID id = service.create(getSampleDTO()).getId();
        service.delete(id);
        assertThrows(RuntimeException.class, () -> service.getById(id));
    }

    @Test
    void shouldThrowForMissingBarcode() {
        assertThrows(RuntimeException.class, () -> service.getByBarcode("DOES_NOT_EXIST"));
    }

    @Test
    void shouldSetDefaultStatusIfNull() {
        IngredientRequestDTO dto = getSampleDTO();
        dto.setStatus(null);
        UUID id = service.create(dto).getId();
        assertEquals(IngredientStatus.AVAILABLE, service.getById(id).getStatus());
    }

    @Test
    void createWithExistingBarcodeAddsQuantity() {
        IngredientRequestDTO dto = getSampleDTO();
        UUID firstId = service.create(dto).getId();

        dto.setQuantity(new BigDecimal("1.0"));
        service.create(dto);

        IngredientResponseDTO result = service.getById(firstId);
        assertEquals(new BigDecimal("1.5").stripTrailingZeros(),
                result.getQuantity().stripTrailingZeros());
    }
}
