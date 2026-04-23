package com.autotrack.inventoryservice.controller;

import com.autotrack.inventoryservice.dto.SparePartResponseDTO;
import com.autotrack.inventoryservice.service.SparePartService;
import org.junit.jupiter.api.DisplayName; // NEW IMPORT
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // NEW IMPORT
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SparePartController.class)
@AutoConfigureMockMvc(addFilters = false)
public class SparePartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SparePartService sparePartService;

    @Test
    void getSparePartById_ShouldReturnPart() throws Exception {
        // Arrange
        SparePartResponseDTO response = new SparePartResponseDTO(1L, "Engine Oil", 100, 20, 15.50);
        when(sparePartService.getSparePartById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/inventoryservice/spare-parts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Engine Oil"))
                .andExpect(jsonPath("$.quantityAvailable").value(100));
    }

    @Test
    void getLowStockParts_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/inventoryservice/spare-parts/low-in-stock"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Add Part - Should fail when name is blank")
    void addSparePart_InvalidName_ReturnsBadRequest() throws Exception {
        String invalidJson = "{\"name\": \"\", \"quantityAvailable\": 10, \"reorderLevel\": 5, \"cost\": 10.0}";

        mockMvc.perform(post("/api/inventoryservice/spare-parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Add Part - Should fail when cost is negative")
    void addSparePart_NegativeCost_ReturnsBadRequest() throws Exception {
        String invalidJson = "{\"name\": \"Gasket\", \"quantityAvailable\": 10, \"reorderLevel\": 5, \"cost\": -5.0}";

        mockMvc.perform(post("/api/inventoryservice/spare-parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}