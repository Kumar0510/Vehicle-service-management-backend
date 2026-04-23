package com.autotrack.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePartResponseDTO {
    private Long partId;
    private String name;
    private Integer quantityAvailable;
    private Integer reorderLevel;
    private Double cost;
//    private Integer standardReorderAmount;
}
