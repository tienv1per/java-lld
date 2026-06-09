package com.tien.lld.ticketbooking.entities;

import com.tien.lld.ticketbooking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Booking {
    private final String id;
    private final User user;
    private final Movie movie;
    private final List<Seat> seats;
    private final double amount;
    private final LocalDateTime createdAt;
    private BookingStatus status;

    private Booking(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.movie = builder.movie;
        this.seats = Collections.unmodifiableList(new ArrayList<>(builder.seats));
        this.amount = builder.amount;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Movie getMovie() {
        return movie;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public double getAmount() {
        return amount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", movie=" + movie.getTitle() +
                ", seats=" + seats +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }

    public static final class Builder {
        private String id;
        private User user;
        private Movie movie;
        private List<Seat> seats;
        private double amount;
        private BookingStatus status;
        private LocalDateTime createdAt;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder movie(Movie movie) {
            this.movie = movie;
            return this;
        }

        public Builder seats(List<Seat> seats) {
            this.seats = seats;
            return this;
        }

        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }

        public Builder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Booking build() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("booking id is required");
            }
            if (user == null) {
                throw new IllegalArgumentException("booking user is required");
            }
            if (movie == null) {
                throw new IllegalArgumentException("booking movie is required");
            }
            if (seats == null || seats.isEmpty()) {
                throw new IllegalArgumentException("booking seats cannot be empty");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("booking amount must be positive");
            }
            if (status == null) {
                throw new IllegalArgumentException("booking status is required");
            }
            if (createdAt == null) {
                throw new IllegalArgumentException("booking createdAt is required");
            }
            return new Booking(this);
        }
    }
}

