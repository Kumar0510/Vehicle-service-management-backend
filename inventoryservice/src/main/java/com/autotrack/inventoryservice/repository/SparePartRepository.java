package com.autotrack.inventoryservice.repository;

import com.autotrack.inventoryservice.model.SparePart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SparePartRepository extends JpaRepository<SparePart, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SparePart s SET s.quantityAvailable = s.quantityAvailable - :quantity WHERE s.partId = :id and s.quantityAvailable >= :quantity ")
    int safeDeductStock(@Param("id") long id, @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE SparePart s SET s.quantityAvailable = s.quantityAvailable + :quantity WHERE s.partId = :id")
    int safeReorderStock(@Param("id") long id, @Param("quantity") int quantity);

    @Query("SELECT s FROM SparePart s where s.quantityAvailable <= s.reorderLevel")
    List<SparePart> getAllLowStockParts();

    List<SparePart> findByPartIdIn(List<Long> partIds);
}