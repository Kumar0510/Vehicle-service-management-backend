package com.autotrack.inventoryservice.repository;

import com.autotrack.inventoryservice.model.SparePart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource; // NEW IMPORT

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
// THIS IS THE FIX: It forces Hibernate to speak "H2" instead of "MySQL" for this test only
@TestPropertySource(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class SparePartRepositoryTest {

    @Autowired
    private SparePartRepository repository;

    @Test
    @DisplayName("Actual SQL - safeDeductStock should accurately decrement")
    void testRealSqlDeduction() {
        SparePart savedPart = repository.save(new SparePart(null, "Alternator", 10, 2, 150.0));

        int rowsUpdated = repository.safeDeductStock(savedPart.getPartId(), 3);

        assertEquals(1, rowsUpdated);
        SparePart updated = repository.findById(savedPart.getPartId()).get();
        assertEquals(7, updated.getQuantityAvailable());
    }

    @Test
    @DisplayName("Actual SQL - getAllLowStockParts should only return items below reorder level")
    void testLowStockQuery() {
        repository.save(new SparePart(null, "LowPart", 5, 10, 1.0));
        repository.save(new SparePart(null, "GoodPart", 20, 10, 1.0));

        List<SparePart> lowStock = repository.getAllLowStockParts();

        assertEquals(1, lowStock.size());
        assertEquals("LowPart", lowStock.get(0).getName());
    }
}