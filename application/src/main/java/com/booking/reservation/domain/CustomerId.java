package com.booking.reservation.domain;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId{
        Objects.requireNonNull(value, "Customer Id cannot be null");
    }

    public static CustomerId random(){
        return new CustomerId(UUID.randomUUID());
    }
}
