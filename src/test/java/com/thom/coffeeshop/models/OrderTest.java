package com.thom.coffeeshop.models;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void constructsWithId() {
        Order order = new Order(42);
        assertEquals(42, order.getId());
        assertNull(order.getStartTime());
        assertNull(order.getFinishTime());
    }

    @Test
    void markStartedNowSetsStartTime() {
        Order order = new Order(1);
        order.markStartedNow();
        assertNotNull(order.getStartTime());
    }

    @Test
    void markFinishedNowSetsFinishTime() {
        Order order = new Order(1);
        order.markFinishedNow();
        assertNotNull(order.getFinishTime());
    }

    @Test
    void allowsExplicitStartAndFinishTimes() {
        Order order = new Order(1);
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        Instant finish = Instant.parse("2026-01-01T00:00:01Z");

        order.setStartTime(start);
        order.setFinishTime(finish);

        assertEquals(start, order.getStartTime());
        assertEquals(finish, order.getFinishTime());
    }

    @Test
    void rejectsNullTimes() {
        Order order = new Order(1);
        assertThrows(NullPointerException.class, () -> order.setStartTime(null));
        assertThrows(NullPointerException.class, () -> order.setFinishTime(null));
    }
}
