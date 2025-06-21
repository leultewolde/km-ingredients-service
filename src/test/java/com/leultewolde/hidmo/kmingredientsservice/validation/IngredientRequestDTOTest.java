package com.leultewolde.hidmo.kmingredientsservice.validation;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class IngredientRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testInvalidIngredientRequest() {
        IngredientRequestDTO dto = new IngredientRequestDTO();
        Set<ConstraintViolation<IngredientRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations for missing required fields");
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));
    }

    @Test
    void testValidIngredientRequest() {
        IngredientRequestDTO dto = new IngredientRequestDTO(
                "Salt",
                new BigDecimal("2.5"),
                "kg",
                new BigDecimal("5.00"),
                LocalDate.now(),
                LocalDate.now().plusDays(90),
                "Pantry",
                IngredientStatus.AVAILABLE,
                "1234567890",
                "/images/salt.png"
        );
        Set<ConstraintViolation<IngredientRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No violations expected for a valid DTO");
    }
}
