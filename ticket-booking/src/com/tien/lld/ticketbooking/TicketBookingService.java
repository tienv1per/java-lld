package com.tien.lld.ticketbooking;

import com.tien.lld.ticketbooking.entities.Booking;
import com.tien.lld.ticketbooking.entities.Movie;
import com.tien.lld.ticketbooking.entities.Seat;
import com.tien.lld.ticketbooking.entities.User;
import com.tien.lld.ticketbooking.exceptions.InvalidRequestException;
import com.tien.lld.ticketbooking.strategy.PricingStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TicketBookingService {
    private final Movie movie;
    private final Map<String, Seat> seatsById;
    private final BookingManager bookingManager;

    public TicketBookingService(
            Movie movie,
            List<Seat> seats,
            PricingStrategy pricingStrategy,
            PaymentService paymentService
    ) {
        if (movie == null) {
            throw new InvalidRequestException("movie is required");
        }
        this.movie = movie;
        this.seatsById = buildSeatMap(seats);
        this.bookingManager = new BookingManager(
                movie,
                seatsById,
                new LinkedHashMap<>(),
                pricingStrategy,
                paymentService,
                new SeatLockManager()
        );
    }

    public Movie getMovie() {
        return movie;
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> availableSeats = new ArrayList<>();
        for (Seat seat : seatsById.values()) {
            if (seat.isAvailable()) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }

    public Booking bookTickets(User user, List<String> seatIds) {
        return bookingManager.bookTickets(user, seatIds);
    }

    public Booking cancelBooking(String bookingId) {
        return bookingManager.cancelBooking(bookingId);
    }

    private Map<String, Seat> buildSeatMap(List<Seat> seats) {
        if (seats == null || seats.isEmpty()) {
            throw new InvalidRequestException("seats cannot be null or empty");
        }

        Map<String, Seat> seatMap = new LinkedHashMap<>();
        for (Seat seat : seats) {
            if (seat == null) {
                throw new InvalidRequestException("seat cannot be null");
            }
            if (seatMap.containsKey(seat.getId())) {
                throw new InvalidRequestException("duplicate seat id: " + seat.getId());
            }
            seatMap.put(seat.getId(), seat);
        }
        return seatMap;
    }
}
