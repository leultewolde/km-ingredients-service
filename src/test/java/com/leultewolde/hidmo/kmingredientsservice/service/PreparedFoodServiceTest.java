package com.leultewolde.hidmo.kmingredientsservice.service;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientUsageRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.request.PreparedFoodRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientUsageResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.PreparedFoodResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
import com.leultewolde.hidmo.kmingredientsservice.mapper.IngredientUsageMapper;
import com.leultewolde.hidmo.kmingredientsservice.mapper.PreparedFoodMapper;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientUsage;
import com.leultewolde.hidmo.kmingredientsservice.model.PreparedFood;
import com.leultewolde.hidmo.kmingredientsservice.repository.IngredientRepository;
import com.leultewolde.hidmo.kmingredientsservice.repository.PreparedFoodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreparedFoodServiceTest {

    @Mock private PreparedFoodRepository preparedFoodRepo;
    @Mock private IngredientRepository ingredientRepo;
    @Mock private PreparedFoodMapper preparedFoodMapper;
    @Mock private IngredientUsageMapper ingredientUsageMapper;
    @Mock private SimpMessagingTemplate template;
    @InjectMocks private PreparedFoodService service;

    @Test
    void shouldCreatePreparedFood() {
        UUID ingId = UUID.randomUUID();
        Ingredient ing = new Ingredient();
        ing.setId(ingId);
        ing.setName("Tomato");
        ing.setQuantity(new BigDecimal("3"));

        IngredientUsageResponseDTO usageDTO = new IngredientUsageResponseDTO();
        usageDTO.setIngredientId(ingId);
        usageDTO.setQuantity(new BigDecimal("2"));

        IngredientUsageRequestDTO usageRequestDTO = new IngredientUsageRequestDTO();
        usageRequestDTO.setIngredientId(ingId);
        usageRequestDTO.setQuantity(new BigDecimal("2"));

        PreparedFoodResponseDTO dto = new PreparedFoodResponseDTO();
        dto.setName("Tomato Sauce");
        dto.setQuantity(new BigDecimal("1"));
        dto.setUnit("jar");
        dto.setDatePrepared(LocalDate.now());
        dto.setExpiryDate(LocalDate.now().plusDays(7));
        dto.setStorageLocation("fridge");
        dto.setIngredientsUsed(List.of(usageDTO));

        PreparedFoodRequestDTO requestDTO = new PreparedFoodRequestDTO();
        requestDTO.setName("Tomato Sauce");
        requestDTO.setQuantity(new BigDecimal("1"));
        requestDTO.setUnit("jar");
        requestDTO.setDatePrepared(LocalDate.now());
        requestDTO.setExpiryDate(LocalDate.now().plusDays(7));
        requestDTO.setStorageLocation("fridge");
        requestDTO.setIngredientsUsed(List.of(usageRequestDTO));

        PreparedFood prep = new PreparedFood();
        prep.setName("Tomato Sauce");
        prep.setQuantity(new BigDecimal("1"));
        prep.setUnit("jar");
        prep.setDatePrepared(LocalDate.now());
        prep.setExpiryDate(LocalDate.now().plusDays(7));
        prep.setStorageLocation("fridge");
        prep.setIngredientsUsed(List.of(new IngredientUsage()));

        when(ingredientRepo.findById(ingId)).thenReturn(Optional.of(ing));
        when(ingredientRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(preparedFoodRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(preparedFoodRepo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(preparedFoodMapper.toEntity(any())).thenReturn(prep);
        when(preparedFoodMapper.toDTO(any())).thenReturn(dto);
        when(ingredientUsageMapper.toEntity(any(), any())).thenReturn(new IngredientUsage());

        PreparedFoodResponseDTO result = service.createPreparedFood(requestDTO);

        assertEquals("Tomato Sauce", result.getName());
        assertEquals(1, result.getIngredientsUsed().size());
        verify(preparedFoodRepo).save(any());
        assertEquals(new BigDecimal("1"), ing.getQuantity());
        verify(template).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void shouldGetAllPreparedFoods() {
        PreparedFood food = new PreparedFood();
        food.setId(UUID.randomUUID());
        food.setName("Leftovers");

        PreparedFoodResponseDTO dto = new PreparedFoodResponseDTO();
        dto.setName("Leftovers");

        when(preparedFoodRepo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(food)));
        when(preparedFoodMapper.toDTO(any())).thenReturn(dto);

        List<PreparedFoodResponseDTO> result = service.getAllPreparedFoods(org.springframework.data.domain.Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals("Leftovers", result.getFirst().getName());
    }

    @Test
    void shouldFindIngredientByID() {
        UUID id = UUID.randomUUID();

        PreparedFood food = new PreparedFood();
        food.setId(id);
        food.setName("Leftovers");

        PreparedFoodResponseDTO dto = new PreparedFoodResponseDTO();
        dto.setId(id);
        dto.setName("Leftovers");

        when(preparedFoodRepo.findById(any(UUID.class))).thenReturn(Optional.of(food));
        when(preparedFoodMapper.toDTO(any())).thenReturn(dto);

        PreparedFoodResponseDTO result = service.getById(id);
        assertEquals(id, result.getId());
        assertEquals("Leftovers", result.getName());
    }

    @Test
    void shouldNotFindIngredientByUnknownID() {
        UUID id = UUID.randomUUID();

        PreparedFood food = new PreparedFood();
        food.setId(id);
        food.setName("Leftovers");

        PreparedFoodResponseDTO dto = new PreparedFoodResponseDTO();
        dto.setId(id);
        dto.setName("Leftovers");

        when(preparedFoodRepo.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void shouldDeletePreparedFood() {
        UUID foodId = UUID.randomUUID();
        PreparedFood food = new PreparedFood();
        food.setId(foodId);
        Ingredient ingredient = new Ingredient();
        ingredient.setId(UUID.randomUUID());
        ingredient.setQuantity(new BigDecimal("1"));
        IngredientUsage usage = new IngredientUsage();
        usage.setIngredient(ingredient);
        usage.setQuantity(new BigDecimal("1"));
        food.setIngredientsUsed(List.of(usage));

        when(preparedFoodRepo.findById(foodId)).thenReturn(Optional.of(food));
        when(preparedFoodRepo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(ingredientRepo.findById(any())).thenReturn(Optional.of(ingredient));
        when(ingredientRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        service.deletePreparedFood(foodId);

        verify(preparedFoodRepo).deleteById(foodId);
        assertEquals(new BigDecimal("2"), ingredient.getQuantity());
        verify(template).convertAndSend(anyString(), any(Object.class));
    }
}
