package com.booking.reservation.domain;

import java.util.Objects;
import java.util.UUID;

public record HoldId(UUID value) {

    public HoldId {
        Objects.requireNonNull(value, "Hold ID must not be null");
    }

    public static HoldId random() {
        return new HoldId(UUID.randomUUID());
    }
}