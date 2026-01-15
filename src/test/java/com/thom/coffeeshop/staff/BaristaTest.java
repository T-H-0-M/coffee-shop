package com.thom.coffeeshop.staff;

import com.thom.coffeeshop.models.Order;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class BaristaTest {

    @Test
    void brewsOrderAndSetsFinishTime() throws Exception {
        BlockingQueue<Order> bench = new LinkedBlockingQueue<>(10);
        Order poisonPill = new Order(-1);

        Order order = new Order(1);

        var executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> future = executor.submit(new Barista(1, bench, 10, poisonPill));

            bench.put(order);
            order.markStartedNow();
            bench.put(poisonPill);

            future.get(1, TimeUnit.SECONDS);

            assertNotNull(order.getFinishTime());
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void exitsOnPoisonPill() throws Exception {
        BlockingQueue<Order> bench = new LinkedBlockingQueue<>(10);
        Order poisonPill = new Order(-1);

        var executor = Executors.newSingleThreadExecutor();
        try {
            bench.put(poisonPill);
            Future<?> future = executor.submit(new Barista(1, bench, 1, poisonPill));

            future.get(1, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void processesOrdersInFifoOrderWithSingleBarista() throws Exception {
        BlockingQueue<Order> bench = new LinkedBlockingQueue<>(10);
        Order poisonPill = new Order(-1);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        var executor = Executors.newSingleThreadExecutor();
        try {
            Future<?> future = executor.submit(new Barista(1, bench, 1, poisonPill));

            for (int i = 1; i <= 3; i++) {
                Order order = new Order(i);
                bench.put(order);
                order.markStartedNow();
            }
            bench.put(poisonPill);

            future.get(2, TimeUnit.SECONDS);

            Pattern idPattern = Pattern.compile("Order\\{id=(\\-?\\d+)");
            Matcher matcher = idPattern.matcher(output.toString(StandardCharsets.UTF_8));

            List<Integer> finishedIds = new ArrayList<>();
            while (matcher.find()) {
                finishedIds.add(Integer.parseInt(matcher.group(1)));
            }

            assertEquals(List.of(1, 1, 2, 2, 3, 3), finishedIds);
        } finally {
            executor.shutdownNow();
            System.setOut(originalOut);
        }
    }
}
