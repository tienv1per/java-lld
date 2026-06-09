package com.tien.lld.ticketbooking;

import java.util.function.Supplier;

public final class SeatLockManager {
    public synchronized <T> T withLock(Supplier<T> action) {
        return action.get();
    }
}

