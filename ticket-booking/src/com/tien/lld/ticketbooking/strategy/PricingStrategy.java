package com.tien.lld.ticketbooking.strategy;

import com.tien.lld.ticketbooking.entities.Seat;

import java.util.List;

public interface PricingStrategy {
    double calculatePrice(List<Seat> seats);
}

