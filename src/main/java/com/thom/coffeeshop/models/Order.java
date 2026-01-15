package com.thom.coffeeshop.models;

import java.time.Instant;
import java.util.Objects;

public final class Order {
    private final int id;
    private volatile Instant startTime;
    private volatile Instant finishTime;

    public Order(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getFinishTime() {
        return finishTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = Objects.requireNonNull(startTime, "startTime");
    }

    public void setFinishTime(Instant finishTime) {
        this.finishTime = Objects.requireNonNull(finishTime, "finishTime");
    }

    public void markStartedNow() {
        setStartTime(Instant.now());
    }

    public void markFinishedNow() {
        setFinishTime(Instant.now());
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                '}';
    }
}
