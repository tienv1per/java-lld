# Movie Ticket Booking

Plain Java implementation for a Low Level Design / Machine Coding interview exercise.

## Scope

This version models booking seats for one movie screening. It intentionally skips theater, screen, and show modeling so the code can focus on the core workflow:

- Create a movie.
- Create simple seats.
- View available seats.
- Book multiple seats.
- Mock payment.
- Prevent double booking.
- Cancel booking.
- Handle validation and edge cases.

## Design

```text
com.tien.lld.ticketbooking
├── Main.java
├── TicketBookingService.java
├── BookingManager.java
├── SeatLockManager.java
├── PaymentService.java
├── entities/
│   ├── Movie.java
│   ├── Seat.java
│   ├── User.java
│   └── Booking.java
├── enums/
│   ├── SeatType.java
│   ├── SeatStatus.java
│   ├── BookingStatus.java
│   └── PaymentStatus.java
├── strategy/
│   ├── PricingStrategy.java
│   └── SeatTypePricingStrategy.java
└── exceptions/
    ├── BookingException.java
    ├── InvalidRequestException.java
    ├── SeatNotAvailableException.java
    ├── BookingNotFoundException.java
    └── PaymentFailedException.java
```

## Patterns Used

- Builder Pattern: `Movie`, `User`, and `Booking`.
- Strategy Pattern: pricing by seat type.
- Facade: `TicketBookingService` exposes a simple API for the demo.

## Run

From repository root:

```bash
javac -d /private/tmp/java-lld-ticket-booking-out \
  $(find ticket-booking/src -name "*.java")

java -cp /private/tmp/java-lld-ticket-booking-out com.tien.lld.ticketbooking.Main
```

