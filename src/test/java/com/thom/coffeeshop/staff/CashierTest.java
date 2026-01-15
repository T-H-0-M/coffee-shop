package com.thom.coffeeshop.staff;

import com.thom.coffeeshop.models.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CashierTest {

    @Test
    void placeOrdersEnqueuesOrdersAndPoisonPillsAndSetsQueuedTime() throws InterruptedException {
        BlockingQueue<Order> bench = new LinkedBlockingQueue<>(100);
        AtomicInteger nextOrderId = new AtomicInteger(0);

        int totalOrders = 5;
        int baristaCount = 3;
        Order poisonPill = new Order(-1);

        Cashier cashier = new Cashier(bench, nextOrderId, totalOrders, baristaCount, poisonPill);
        cashier.placeOrders();

        assertEquals(totalOrders + baristaCount, bench.size());

        List<Order> drained = new ArrayList<>();
        bench.drainTo(drained);

        assertEquals(totalOrders + baristaCount, drained.size());

        for (int i = 0; i < totalOrders; i++) {
            Order order = drained.get(i);
            assertEquals(i + 1, order.getId());
            assertNotNull(order.getStartTime(), "startTime should be set after successful queueing");
            assertNull(order.getFinishTime(), "finishTime should not be set by cashier");
            assertNotSame(poisonPill, order);
        }

        for (int i = totalOrders; i < totalOrders + baristaCount; i++) {
            assertSame(poisonPill, drained.get(i), "poison pill should be the same sentinel instance");
        }
    }

    @Test
    void placeOrdersBlocksWhenBenchIsFull() throws Exception {
        BlockingQueue<Order> bench = new LinkedBlockingQueue<>(1);
        AtomicInteger nextOrderId = new AtomicInteger(0);

        Order poisonPill = new Order(-1);
        Cashier cashier = new Cashier(bench, nextOrderId, 2, 0, poisonPill);

        var executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> future = executor.submit(() -> {
                try {
                    cashier.placeOrders();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            assertThrows(TimeoutException.class, () -> future.get(200, TimeUnit.MILLISECONDS));
            assertEquals(1, bench.size());

            bench.take();

            assertDoesNotThrow(() -> future.get(1, TimeUnit.SECONDS));

            assertEquals(1, bench.size(), "second order should be queued after space is freed");
            bench.take();
        } finally {
            executor.shutdownNow();
        }
    }
}
