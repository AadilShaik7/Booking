package com.booking.reservation.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public record Booking(
        BookingId id,
        HoldId holdId,
        EventId eventId,
        CustomerId customerId,
        Set<SeatId> seatIds,
        Instant confirmedAt
) {

    public Booking {
        Objects.requireNonNull(id, "Booking ID must not be null");
        Objects.requireNonNull(holdId, "Hold ID must not be null");
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(customerId, "Customer ID must not be null");
        Objects.requireNonNull(seatIds, "Seat IDs must not be null");
        Objects.requireNonNull(confirmedAt, "Confirmation time must not be null");
        if (seatIds.isEmpty()) {
            throw new IllegalArgumentException("A booking must contain at least one seat");
        }
        seatIds = Set.copyOf(seatIds);
    }

}
