
package com.leultewolde.hidmo.kmingredientsservice.validation;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class IngredientUsageRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testInvalidIngredientUsageRequest() {
        IngredientUsageRequestDTO dto = new IngredientUsageRequestDTO();
        Set<ConstraintViolation<IngredientUsageRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations for missing required fields");
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));
    }

    @Test
    void testValidIngredientUsageRequest() {
        IngredientUsageRequestDTO dto = new IngredientUsageRequestDTO(UUID.randomUUID(), new BigDecimal("0.5"));
        Set<ConstraintViolation<IngredientUsageRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No violations expected for a valid DTO");
    }
}
