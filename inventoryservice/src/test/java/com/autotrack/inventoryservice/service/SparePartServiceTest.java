package com.autotrack.inventoryservice.service;

import com.autotrack.inventoryservice.config.SparePartMapper;
import com.autotrack.inventoryservice.exception.SparePartException;
import com.autotrack.inventoryservice.model.SparePart;
import com.autotrack.inventoryservice.repository.SparePartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)
public class SparePartServiceTest {

    @Mock
    private SparePartRepository repo;

    @Mock
    private SparePartMapper mapper;

    @InjectMocks
    private SparePartService sparePartService;

    @Test
    @DisplayName("Deduct Stock - Should throw exception when stock is insufficient")
    void deductStock_InsufficientStock_ThrowsException() {
        long partId = 1L;
        int quantityToDeduct = 50;

        when(repo.safeDeductStock(partId, quantityToDeduct)).thenReturn(0);
        when(repo.findById(partId)).thenReturn(Optional.of(new SparePart()));

        assertThrows(SparePartException.class, () -> {
            sparePartService.deductStock(partId, quantityToDeduct);
        });
    }

    @Test
    @DisplayName("Get Part By ID - Should throw exception when part does not exist")
    void getSparePartById_NotFound_ThrowsException() {
        long partId = 99L;
        when(repo.findById(partId)).thenReturn(Optional.empty());

        SparePartException exception = assertThrows(SparePartException.class, () -> {
            sparePartService.getSparePartById(partId);
        });
        assertTrue(exception.getMessage().contains("Cannot fetch SparePart"));
    }

    @Test
    @DisplayName("Reorder Stock - Should succeed when repository updates rows")
    void reorderStockLevels_Success() {
        long sid = 1L;
        int amount = 10;

        when(repo.safeReorderStock(sid, amount)).thenReturn(1);

        assertDoesNotThrow(() -> sparePartService.reorderStockLevels(sid, amount));
        verify(repo, times(1)).safeReorderStock(sid, amount);
    }

    @Test
    @DisplayName("Concurrency - Prevent Race Condition on Last Item")
    void deductStock_ConcurrentThreads_OnlyOneSucceeds() throws InterruptedException {
        long partId = 1L;
        when(repo.safeDeductStock(partId, 1))
                .thenReturn(1)
                .thenReturn(0);

        when(repo.findById(partId)).thenReturn(Optional.of(new SparePart()));
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                sparePartService.deductStock(partId, 1);
                successCount.incrementAndGet();
            } catch (SparePartException e) {
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        executor.submit(task);
        executor.submit(task);
        latch.await();

        assertEquals(1, successCount.get());
        assertEquals(1, failCount.get());
    }

    @Test
    @DisplayName("Deduct Stock - Should succeed when deducting exact remaining amount")
    void deductStock_ExactAmount_Succeeds() {
        long partId = 1L;
        int exactRemainingAmount = 5;

        // The magic logic: if they ask for 5, and we have 5, it should return 1 (success)
        when(repo.safeDeductStock(partId, exactRemainingAmount)).thenReturn(1);

        assertDoesNotThrow(() -> sparePartService.deductStock(partId, exactRemainingAmount));
        verify(repo, times(1)).safeDeductStock(partId, exactRemainingAmount);
    }
}