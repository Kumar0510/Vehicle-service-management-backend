package com.autotrack.inventoryservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.id.IncrementGenerator;

@Entity
@Table(name = "spare_parts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparePart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Column(nullable = false)
    private Integer reorderLevel;

    @Column(nullable = false)
    private Double cost;

}