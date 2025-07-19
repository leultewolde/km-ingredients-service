package com.leultewolde.hidmo.kmingredientsservice.service;

import com.leultewolde.hidmo.kmingredientsservice.dto.request.IngredientRequestDTO;
import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
import com.leultewolde.hidmo.kmingredientsservice.mapper.IngredientMapper;
import com.leultewolde.hidmo.kmingredientsservice.model.Ingredient;
import com.leultewolde.hidmo.kmingredientsservice.model.IngredientStatus;
import com.leultewolde.hidmo.kmingredientsservice.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock private IngredientRepository repo;
    @Mock private IngredientMapper mapper;
    @Mock private SimpMessagingTemplate template;
    @InjectMocks private IngredientService service;

    @Test
    void shouldCreateIngredient() {
        IngredientRequestDTO dto = new IngredientRequestDTO();
        dto.setName("Apple");
        dto.setQuantity(new BigDecimal("5"));
        dto.setUnit("pcs");
        dto.setStatus(IngredientStatus.AVAILABLE);

        Ingredient ing = new Ingredient();
        ing.setName("Apple");
        ing.setQuantity(new BigDecimal("5"));

        Ingredient saved = new Ingredient();
        saved.setId(UUID.randomUUID());
        saved.setName("Apple");
        saved.setQuantity(new BigDecimal("5"));

        IngredientResponseDTO responseDTO = new IngredientResponseDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setName("Apple");
        responseDTO.setQuantity(new BigDecimal("5"));

        when(repo.save(any())).thenReturn(saved);
        when(repo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(mapper.toEntity(any())).thenReturn(ing);
        when(mapper.toDTO(any())).thenReturn(responseDTO);

        IngredientResponseDTO result = service.create(dto);

        assertNotNull(result.getId());
        assertEquals("Apple", result.getName());
        verify(repo).save(any());
        verify(template).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void shouldUpdateQuantityWhenBarcodeExists() {
        IngredientRequestDTO dto = new IngredientRequestDTO();
        dto.setName("Apple");
        dto.setQuantity(new BigDecimal("5"));
        dto.setBarcode("123");

        Ingredient existing = new Ingredient();
        existing.setId(UUID.randomUUID());
        existing.setName("Apple");
        existing.setQuantity(new BigDecimal("2"));
        existing.setBarcode("123");
        existing.setStatus(IngredientStatus.AVAILABLE);

        Ingredient updated = new Ingredient();
        updated.setId(existing.getId());
        updated.setName("Apple");
        updated.setQuantity(new BigDecimal("7"));
        updated.setBarcode("123");
        updated.setStatus(IngredientStatus.AVAILABLE);

        IngredientResponseDTO dtoResp = new IngredientResponseDTO();
        dtoResp.setId(existing.getId());
        dtoResp.setName("Apple");
        dtoResp.setQuantity(new BigDecimal("7"));

        when(repo.findByBarcode("123")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(updated);
        when(repo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(mapper.toDTO(updated)).thenReturn(dtoResp);

        IngredientResponseDTO result = service.create(dto);

        assertEquals(new BigDecimal("7"), result.getQuantity());
        verify(repo).save(existing);
        verify(template).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void shouldReturnAllIngredients() {
        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setId(UUID.randomUUID());
        dto.setName("Salt");

        Ingredient ing = new Ingredient();
        ing.setId(UUID.randomUUID());
        ing.setName("Salt");

        when(repo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(ing)));
        when(mapper.toDTO(any())).thenReturn(dto);

        List<IngredientResponseDTO> result = service.getAll(org.springframework.data.domain.Pageable.unpaged());
        assertEquals(1, result.size());
        assertEquals("Salt", result.getFirst().getName());
    }

    @Test
    void shouldFindIngredientByID() {
        UUID id = UUID.randomUUID();

        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setId(id);
        dto.setName("Salt");

        Ingredient ing = new Ingredient();
        ing.setId(UUID.randomUUID());
        ing.setName("Salt");

        when(repo.findById(any(UUID.class))).thenReturn(Optional.of(ing));
        when(mapper.toDTO(any())).thenReturn(dto);

        IngredientResponseDTO result = service.getById(id);
        assertEquals(id, result.getId());
        assertEquals("Salt", result.getName());
    }

    @Test
    void shouldNotFindIngredientByUnknownID() {
        UUID id = UUID.randomUUID();

        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setId(id);
        dto.setName("Salt");

        Ingredient ing = new Ingredient();
        ing.setId(UUID.randomUUID());
        ing.setName("Salt");

        when(repo.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(id));
    }

    @Test
    void shouldDeleteIngredient() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(true);
        when(repo.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        service.delete(id);
        verify(repo).deleteById(id);
        verify(template).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void deleteUnknownIngredientThrows() {
        UUID id = UUID.randomUUID();
        when(repo.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(id));
    }
}

