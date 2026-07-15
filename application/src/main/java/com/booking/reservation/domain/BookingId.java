package com.booking.reservation.domain;

import java.util.Objects;
import java.util.UUID;

public record BookingId(UUID value) {

    public BookingId {
        Objects.requireNonNull(value, "Booking ID must not be null");
    }

    public static BookingId random() {
        return new BookingId(UUID.randomUUID());
    }
}