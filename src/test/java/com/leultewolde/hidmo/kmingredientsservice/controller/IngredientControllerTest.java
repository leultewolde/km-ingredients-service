package com.leultewolde.hidmo.kmingredientsservice.controller;

import com.leultewolde.hidmo.kmingredientsservice.dto.response.IngredientResponseDTO;
import com.leultewolde.hidmo.kmingredientsservice.exception.ResourceNotFoundException;
import com.leultewolde.hidmo.kmingredientsservice.service.IngredientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IngredientController.class)
class IngredientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IngredientService service;

    @Test
    void shouldReturnAllIngredients() throws Exception {
        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setName("Sugar");

        when(service.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/v1/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sugar"));
    }

    @Test
    void shouldCreateIngredient() throws Exception {
        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setName("Flour");
        dto.setId(UUID.randomUUID());

        when(service.create(any())).thenReturn(dto);

        mockMvc.perform(post("/v1/ingredients")
                        .contentType("application/json")
                        .content("""
                            {
                              "name": "Flour",
                              "quantity": 2,
                              "unit": "kg",
                              "price": 10,
                              "dateBought": "2025-06-15",
                              "expiryDate": "2025-06-18"
                            }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(dto.getId().toString()));
    }

    @Test
    void shouldReturnIngredientById() throws Exception {
        UUID id = UUID.randomUUID();
        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setId(id);
        dto.setName("Salt");

        when(service.getById(id)).thenReturn(dto);

        mockMvc.perform(get("/v1/ingredients/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Salt"));
    }

    @Test
    void shouldReturnNotFoundWhenIngredientByIdNotExists() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.getById(id)).thenThrow(new ResourceNotFoundException("Ingredient","id",id));

        mockMvc.perform(get("/v1/ingredients/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnIngredientByBarcode() throws Exception {
        String barcode = "123456789";
        IngredientResponseDTO dto = new IngredientResponseDTO();
        dto.setBarcode(barcode);
        dto.setName("Yeast");

        when(service.getByBarcode(barcode)).thenReturn(dto);

        mockMvc.perform(get("/v1/ingredients/by-barcode/" + barcode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barcode").value("123456789"))
                .andExpect(jsonPath("$.name").value("Yeast"));
    }

    @Test
    void shouldReturnNotFoundWhenBarcodeNotExists() throws Exception {
        String barcode = "000000";
        when(service.getByBarcode(barcode)).thenThrow(new ResourceNotFoundException("Ingredient","barcode",barcode));

        mockMvc.perform(get("/v1/ingredients/by-barcode/" + barcode))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteIngredientById() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(service).delete(id);

        mockMvc.perform(delete("/v1/ingredients/" + id))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(id);
    }
}
