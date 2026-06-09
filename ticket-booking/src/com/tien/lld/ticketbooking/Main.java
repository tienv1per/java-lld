package com.tien.lld.ticketbooking;

import com.tien.lld.ticketbooking.entities.Booking;
import com.tien.lld.ticketbooking.entities.Movie;
import com.tien.lld.ticketbooking.entities.Seat;
import com.tien.lld.ticketbooking.entities.User;
import com.tien.lld.ticketbooking.enums.SeatType;
import com.tien.lld.ticketbooking.exceptions.BookingException;
import com.tien.lld.ticketbooking.strategy.SeatTypePricingStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Movie movie = Movie.builder()
                .id("movie-1")
                .title("Inception")
                .language("English")
                .durationMinutes(148)
                .build();

        User alice = User.builder()
                .id("user-1")
                .name("Alice")
                .build();

        User bob = User.builder()
                .id("user-2")
                .name("Bob")
                .build();

        User paymentFailUser = User.builder()
                .id("payment-fail-user")
                .name("Payment Fail User")
                .build();

        TicketBookingService bookingService = new TicketBookingService(
                movie,
                createSeats(),
                new SeatTypePricingStrategy(),
                new PaymentService()
        );

        System.out.println("Movie: " + bookingService.getMovie());
        printAvailableSeats(bookingService);

        final Booking[] aliceBooking = new Booking[1];

        run("Alice books A1, A2", () -> {
            aliceBooking[0] = bookingService.bookTickets(alice, Arrays.asList("A1", "A2"));
            System.out.println(aliceBooking[0]);
        });
        printAvailableSeats(bookingService);

        run("Bob tries to book already booked A1", () ->
                System.out.println(bookingService.bookTickets(bob, List.of("A1")))
        );

        run("Payment failed user tries to book B1", () ->
                System.out.println(bookingService.bookTickets(paymentFailUser, List.of("B1")))
        );
        printAvailableSeats(bookingService);

        run("Bob books B1, C1", () ->
                System.out.println(bookingService.bookTickets(bob, Arrays.asList("B1", "C1")))
        );
        printAvailableSeats(bookingService);

        run("Cancel Alice booking", () ->
                System.out.println(bookingService.cancelBooking(aliceBooking[0].getId()))
        );
        printAvailableSeats(bookingService);

        run("Cancel Alice booking again", () ->
                System.out.println(bookingService.cancelBooking(aliceBooking[0].getId()))
        );

        run("Book duplicate seats A3, A3", () ->
                System.out.println(bookingService.bookTickets(alice, Arrays.asList("A3", "A3")))
        );

        run("Book invalid seat Z9", () ->
                System.out.println(bookingService.bookTickets(alice, List.of("Z9")))
        );

        run("Book empty seat list", () ->
                System.out.println(bookingService.bookTickets(alice, List.of()))
        );

        run("Book with null user", () ->
                System.out.println(bookingService.bookTickets(null, List.of("A3")))
        );
    }

    private static List<Seat> createSeats() {
        List<Seat> seats = new ArrayList<>();
        seats.add(new Seat("A1", SeatType.REGULAR));
        seats.add(new Seat("A2", SeatType.REGULAR));
        seats.add(new Seat("A3", SeatType.REGULAR));
        seats.add(new Seat("B1", SeatType.PREMIUM));
        seats.add(new Seat("B2", SeatType.PREMIUM));
        seats.add(new Seat("C1", SeatType.RECLINER));
        return seats;
    }

    private static void printAvailableSeats(TicketBookingService bookingService) {
        System.out.println("Available seats: " + bookingService.getAvailableSeats());
    }

    private static void run(String title, DemoAction action) {
        System.out.println();
        System.out.println("Testcase: " + title);
        try {
            action.run();
        } catch (BookingException exception) {
            System.out.println("Failed: " + exception.getMessage());
        }
    }

    @FunctionalInterface
    private interface DemoAction {
        void run();
    }
}
