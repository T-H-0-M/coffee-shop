package com.thom.coffeeshop.staff;

import com.thom.coffeeshop.models.Order;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class Cashier {
    private final BlockingQueue<Order> bench;
    private final AtomicInteger nextOrderId;
    private final int totalOrders;
    private final int baristaCount;
    private final Order poisonPill;

    public Cashier(
            BlockingQueue<Order> bench,
            AtomicInteger nextOrderId,
            int totalOrders,
            int baristaCount,
            Order poisonPill
    ) {
        this.bench = Objects.requireNonNull(bench, "bench");
        this.nextOrderId = Objects.requireNonNull(nextOrderId, "nextOrderId");
        this.totalOrders = totalOrders;
        this.baristaCount = baristaCount;
        this.poisonPill = Objects.requireNonNull(poisonPill, "poisonPill");
    }

    public void placeOrders() throws InterruptedException {
        for (int i = 0; i < totalOrders; i++) {
            Order order = new Order(nextOrderId.incrementAndGet());

            bench.put(order);
            order.markStartedNow();

            System.out.println("Cashier queued " + order);
        }

        for (int i = 0; i < baristaCount; i++) {
            bench.put(poisonPill);
        }
    }
}
