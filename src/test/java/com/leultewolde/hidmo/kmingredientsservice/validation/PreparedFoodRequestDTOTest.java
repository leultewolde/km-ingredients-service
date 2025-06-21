
package com.leultewolde.hidmo.kmingredientsservice.validation;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.model.PreparedFoodStatus;
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

class PreparedFoodRequestDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testInvalidPreparedFoodRequest() {
        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO();
        Set<ConstraintViolation<PreparedFoodRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Expected violations for missing required fields");
        violations.forEach(v -> System.out.println(v.getPropertyPath() + ": " + v.getMessage()));
    }

    @Test
    void testValidPreparedFoodRequest() {
        IngredientUsageRequestDTO usage = new IngredientUsageRequestDTO(UUID.randomUUID(), new BigDecimal("1.0"));
        PreparedFoodRequestDTO dto = new PreparedFoodRequestDTO(
                "Spaghetti Sauce",
                new BigDecimal("2.0"),
                "L",
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Fridge",
                IngredientStatus.AVAILABLE,
                PreparedFoodStatus.STORED,
                Collections.singletonList(usage)
        );
        Set<ConstraintViolation<PreparedFoodRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No violations expected for a valid DTO");
    }
}
