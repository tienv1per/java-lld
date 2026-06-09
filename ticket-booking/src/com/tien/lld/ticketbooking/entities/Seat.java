package com.tien.lld.ticketbooking.entities;

import com.tien.lld.ticketbooking.enums.SeatStatus;
import com.tien.lld.ticketbooking.enums.SeatType;

public final class Seat {
    private final String id;
    private final SeatType type;
    private SeatStatus status;

    public Seat(String id, SeatType type) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("seat id is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("seat type is required");
        }
        this.id = id;
        this.type = type;
        this.status = SeatStatus.AVAILABLE;
    }

    public String getId() {
        return id;
    }

    public SeatType getType() {
        return type;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public boolean isAvailable() {
        return status == SeatStatus.AVAILABLE;
    }

    public void book() {
        this.status = SeatStatus.BOOKED;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}

