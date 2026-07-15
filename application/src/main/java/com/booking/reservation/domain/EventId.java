package com.booking.reservation.domain;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {

    public EventId{
        Objects.requireNonNull(value, "Event Id must not be null");
    }

    public static EventId random() {
        return new EventId(UUID.randomUUID());
    }
}
