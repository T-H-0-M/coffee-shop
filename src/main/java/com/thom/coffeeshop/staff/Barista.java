package com.thom.coffeeshop.staff;

import com.thom.coffeeshop.models.Order;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public final class Barista implements Runnable {
    private final int baristaId;
    private final BlockingQueue<Order> bench;
    private final long brewMillis;
    private final Order poisonPill;

    public Barista(int baristaId, BlockingQueue<Order> bench, long brewMillis, Order poisonPill) {
        this.baristaId = baristaId;
        this.bench = Objects.requireNonNull(bench, "bench");
        this.brewMillis = brewMillis;
        this.poisonPill = Objects.requireNonNull(poisonPill, "poisonPill");
    }

    @Override
    public void run() {
        try {
            while (true) {
                Order order = bench.take();
                if (order == poisonPill) {
                    return;
                }
                System.out.println("Barista #" + baristaId + " started " + order);

                Thread.sleep(brewMillis);
                order.markFinishedNow();

                System.out.println("Barista #" + baristaId + " finished " + order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
