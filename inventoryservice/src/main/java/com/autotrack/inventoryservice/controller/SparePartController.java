package com.autotrack.inventoryservice.controller;

import com.autotrack.inventoryservice.dto.SparePartRequestDTO;
import com.autotrack.inventoryservice.dto.SparePartResponseDTO;
import com.autotrack.inventoryservice.service.SparePartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Spare Parts API", description = "Operations related to managing spare parts inventory")
@RequestMapping("/api/inventoryservice")
public class SparePartController {

    private SparePartService spService;

    @Autowired
    public void setSpService(SparePartService spService){
        this.spService = spService;
    }

    @Operation(summary = "Get all spare parts")
    @GetMapping("/spare-parts")
    public ResponseEntity<List<SparePartResponseDTO>> getAllSpareParts(){
        List<SparePartResponseDTO> parts = spService.getAllSpareParts();
        return ResponseEntity.ok(parts); // Returns 200 OK
    }

    @Operation(summary = "Get a spare part by ID")
    @GetMapping("/spare-parts/{id}")
    public ResponseEntity<SparePartResponseDTO> getSparePartById(
            @Parameter(description = "ID of the spare part") @PathVariable long id){
        SparePartResponseDTO part = spService.getSparePartById(id);
        return ResponseEntity.ok(part); // Returns 200 OK
    }

    @Operation(summary = "Add a new spare part")
    @PostMapping("/spare-parts")
    public ResponseEntity<SparePartResponseDTO> addSparePart(@RequestBody @Valid SparePartRequestDTO sparePart){
        SparePartResponseDTO createdPart = spService.addSparePart(sparePart);
        // Returns 201 Created instead of standard 200
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPart);
    }

    @Operation(summary = "Update an existing spare part")
    @PutMapping("/spare-parts/{id}")
    public ResponseEntity<Void> updateSparePartById(@PathVariable long id, @RequestBody @Valid SparePartRequestDTO updatedPart){
        spService.updateSparePartById(id, updatedPart);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete a spare part")
    @DeleteMapping("/spare-parts/{id}")
    public ResponseEntity<Void> deleteSparePartById(@PathVariable long id){
        spService.deleteSparePartById(id);
        return ResponseEntity.noContent().build(); // Returns 204 No Content
    }

    // user specific
    @Operation(summary = "Get low stock parts")
    @GetMapping("/spare-parts/low-in-stock")
    public ResponseEntity<List<SparePartResponseDTO>> getAllLowStockParts(){
        List<SparePartResponseDTO> lowStockParts = spService.getAllLowStockParts();
        return ResponseEntity.ok(lowStockParts); // Returns 200 OK
    }

    @Operation(summary = "Deduct stock")
    @PutMapping("/spare-parts/{id}/deduct")
    public ResponseEntity<Void> deductStock(
            @Parameter(description = "ID of the spare part") @PathVariable long id,
            @Parameter(description = "Quantity to deduct") @RequestParam int quantity) {
        spService.deductStock(id, quantity);
        return ResponseEntity.ok().build(); // Returns 200 OK
    }

    @Operation(summary = "Reorder stock")
    @PutMapping("/spare-parts/{id}/reorder")
    public ResponseEntity<Void> reorderStock(
            @Parameter(description = "ID of the spare part") @PathVariable long id,
            @Parameter(description = "Amount received") @RequestParam int amount){
        spService.reorderStockLevels(id, amount);
        return ResponseEntity.ok().build(); // Returns 200 OK
    }

    @GetMapping("/spare-parts/bulk-fetch")
    List<SparePartResponseDTO> getSpareParts(@RequestParam("ids") List<Long> parts){
       return spService.getBulkSpareParts(parts);
    }
}