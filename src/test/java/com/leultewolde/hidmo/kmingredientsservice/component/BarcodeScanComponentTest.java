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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BarcodeScanComponentTest {

    @Autowired
    private IngredientService ingredientService;

    @Test
    void shouldFindIngredientByBarcode() {
        String barcode = "SCAN-456";

        IngredientRequestDTO dto = new IngredientRequestDTO(
            "Scanned Item", new BigDecimal("1.0"), "pcs",
            new BigDecimal("1.00"), LocalDate.now(),
            LocalDate.now().plusDays(3), "Shelf",
            IngredientStatus.AVAILABLE, barcode, "/images/scan.png"
        );

        ingredientService.create(dto);

        IngredientResponseDTO result = ingredientService.getByBarcode(barcode);

        assertEquals(barcode, result.getBarcode());
        assertEquals("Scanned Item", result.getName());
    }

    @Test
    void shouldThrowIfBarcodeMissing() {
        assertThrows(RuntimeException.class, () -> ingredientService.getByBarcode("MISSING-BARCODE"));
    }
}
