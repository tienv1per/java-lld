package com.tien.lld.ticketbooking;

import com.tien.lld.ticketbooking.entities.Booking;
import com.tien.lld.ticketbooking.entities.Movie;
import com.tien.lld.ticketbooking.entities.Seat;
import com.tien.lld.ticketbooking.entities.User;
import com.tien.lld.ticketbooking.enums.BookingStatus;
import com.tien.lld.ticketbooking.enums.PaymentStatus;
import com.tien.lld.ticketbooking.exceptions.BookingNotFoundException;
import com.tien.lld.ticketbooking.exceptions.InvalidRequestException;
import com.tien.lld.ticketbooking.exceptions.PaymentFailedException;
import com.tien.lld.ticketbooking.exceptions.SeatNotAvailableException;
import com.tien.lld.ticketbooking.strategy.PricingStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class BookingManager {
    private final Movie movie;
    private final Map<String, Seat> seatsById;
    private final Map<String, Booking> bookingsById;
    private final PricingStrategy pricingStrategy;
    private final PaymentService paymentService;
    private final SeatLockManager seatLockManager;

    public BookingManager(
            Movie movie,
            Map<String, Seat> seatsById,
            Map<String, Booking> bookingsById,
            PricingStrategy pricingStrategy,
            PaymentService paymentService,
            SeatLockManager seatLockManager
    ) {
        if (pricingStrategy == null) {
            throw new InvalidRequestException("pricingStrategy is required");
        }
        if (paymentService == null) {
            throw new InvalidRequestException("paymentService is required");
        }
        if (seatLockManager == null) {
            throw new InvalidRequestException("seatLockManager is required");
        }
        this.movie = movie;
        this.seatsById = seatsById;
        this.bookingsById = bookingsById;
        this.pricingStrategy = pricingStrategy;
        this.paymentService = paymentService;
        this.seatLockManager = seatLockManager;
    }

    public Booking bookTickets(User user, List<String> seatIds) {
        return seatLockManager.withLock(() -> createBooking(user, seatIds));
    }

    public Booking cancelBooking(String bookingId) {
        return seatLockManager.withLock(() -> cancelExistingBooking(bookingId));
    }

    private Booking createBooking(User user, List<String> seatIds) {
        validateUser(user);
        List<Seat> requestedSeats = findAndValidateSeats(seatIds);
        double amount = pricingStrategy.calculatePrice(requestedSeats);

        PaymentStatus paymentStatus = paymentService.pay(user, amount);
        if (paymentStatus != PaymentStatus.SUCCESS) {
            throw new PaymentFailedException("payment failed for user: " + user.getId());
        }

        for (Seat seat : requestedSeats) {
            seat.book();
        }

        Booking booking = Booking.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .movie(movie)
                .seats(requestedSeats)
                .amount(amount)
                .status(BookingStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .build();

        bookingsById.put(booking.getId(), booking);
        return booking;
    }

    private Booking cancelExistingBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) {
            throw new InvalidRequestException("bookingId is required");
        }

        Booking booking = bookingsById.get(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException("booking not found: " + bookingId);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidRequestException("booking is already cancelled: " + bookingId);
        }

        for (Seat seat : booking.getSeats()) {
            seat.release();
        }
        booking.cancel();
        return booking;
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new InvalidRequestException("user is required");
        }
    }

    private List<Seat> findAndValidateSeats(List<String> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new InvalidRequestException("seatIds cannot be null or empty");
        }

        Set<String> uniqueSeatIds = new HashSet<>();
        List<Seat> seats = new ArrayList<>();

        for (String seatId : seatIds) {
            if (seatId == null || seatId.trim().isEmpty()) {
                throw new InvalidRequestException("seatId cannot be null or blank");
            }
            if (!uniqueSeatIds.add(seatId)) {
                throw new InvalidRequestException("duplicate seat in request: " + seatId);
            }

            Seat seat = seatsById.get(seatId);
            if (seat == null) {
                throw new InvalidRequestException("seat does not exist: " + seatId);
            }
            if (!seat.isAvailable()) {
                throw new SeatNotAvailableException("seat is not available: " + seatId);
            }
            seats.add(seat);
        }

        return seats;
    }
}
