package com.thom.coffeeshop;

import com.thom.coffeeshop.models.Order;
import com.thom.coffeeshop.staff.Barista;
import com.thom.coffeeshop.staff.Cashier;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    private static final int BARISTA_COUNT = 3;
    private static final int BENCH_CAPACITY = 5;
    private static final int TOTAL_ORDERS = 15;
    private static final long BREW_MILLIS = 2_000;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Order> bench = new LinkedBlockingQueue<>(BENCH_CAPACITY);
        ExecutorService baristas = Executors.newFixedThreadPool(BARISTA_COUNT);

        Order poisonPill = new Order(-1);
        for (int i = 0; i < BARISTA_COUNT; i++) {
            baristas.submit(new Barista(i + 1, bench, BREW_MILLIS, poisonPill));
        }

        Cashier cashier = new Cashier(bench, new AtomicInteger(0), TOTAL_ORDERS, BARISTA_COUNT, poisonPill);
        cashier.placeOrders();

        baristas.shutdown();
        baristas.awaitTermination(1, TimeUnit.MINUTES);
    }
}
