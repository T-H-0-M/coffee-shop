package com.thom.coffeeshop;

import com.thom.coffeeshop.models.Order;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BenchTest {

    @Test
    void linkedBlockingQueueIsFifo() throws InterruptedException {
        BlockingQueue<Order> bench = new LinkedBlockingQueue<>(10);

        bench.put(new Order(1));
        bench.put(new Order(2));
        bench.put(new Order(3));

        assertEquals(1, bench.take().getId());
        assertEquals(2, bench.take().getId());
        assertEquals(3, bench.take().getId());
    }
}
