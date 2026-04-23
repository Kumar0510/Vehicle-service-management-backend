package com.autotrack.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartRequestDTO {

    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be blank")
    private String name;

    @Min(value=0, message = "quantity cannot be negative")
    private Integer quantityAvailable;

    @Min(value=0, message = "reorder cannot be negative")
    private Integer reorderLevel;

    @Min(value=0, message = "cost quantity cannot be negative")
    private Double cost;

//    @Min(value=1, message = "Standard reorder amount cannot be negative")
//    private Integer standardReorderAmount;
}
