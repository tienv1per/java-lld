package com.tien.lld.ticketbooking.strategy;

import com.tien.lld.ticketbooking.entities.Seat;
import com.tien.lld.ticketbooking.enums.SeatType;
import com.tien.lld.ticketbooking.exceptions.InvalidRequestException;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class SeatTypePricingStrategy implements PricingStrategy {
    private final Map<SeatType, Double> priceBySeatType = new EnumMap<>(SeatType.class);

    public SeatTypePricingStrategy() {
        priceBySeatType.put(SeatType.REGULAR, 100.0);
        priceBySeatType.put(SeatType.PREMIUM, 150.0);
        priceBySeatType.put(SeatType.RECLINER, 250.0);
    }

    @Override
    public double calculatePrice(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            throw new InvalidRequestException("seats cannot be null or empty for pricing");
        }

        double total = 0;
        for (Seat seat : seats) {
            if (seat == null) {
                throw new InvalidRequestException("seat cannot be null for pricing");
            }
            Double price = priceBySeatType.get(seat.getType());
            if (price == null) {
                throw new InvalidRequestException("price is not configured for seat type: " + seat.getType());
            }
            total += price;
        }
        return total;
    }
}

