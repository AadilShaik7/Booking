package com.booking.reservation.domain;

import java.util.Objects;
import java.util.UUID;

public record SeatId(UUID value) {

    public SeatId{
        Objects.requireNonNull(value, "Seat Id must not be null");
    }

    public static SeatId random() {
        return new SeatId(UUID.randomUUID());
    }
}
