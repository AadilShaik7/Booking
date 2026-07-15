package com.booking.reservation.domain;

import com.booking.reservation.exception.InvalidHoldStateException;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

public record SeatHold(HoldId id,
                       EventId eventId,
                       CustomerId customerId,
                       Set<SeatId> seatIds,
                       HoldStatus status,
                       Instant createdAt) {
    public SeatHold {
        Objects.requireNonNull(id, "Hold ID must not be null");
        Objects.requireNonNull(eventId, "Event ID must not be null");
        Objects.requireNonNull(customerId, "Customer ID must not be null");
        Objects.requireNonNull(seatIds, "Seat IDs must not be null");
        Objects.requireNonNull(status, "Hold status must not be null");
        Objects.requireNonNull(createdAt, "Created time must not be null");
        if (seatIds.isEmpty()) {
            throw new IllegalArgumentException("A hold must contain at least one seat");
        }
        seatIds = Set.copyOf(seatIds);
    }

    public static SeatHold active(
            HoldId holdId,
            EventId eventId,
            CustomerId customerId,
            Set<SeatId> seatIds,
            Instant createdAt
    ) {
        return new SeatHold(
                holdId,
                eventId,
                customerId,
                seatIds,
                HoldStatus.ACTIVE,
                createdAt
        );
    }

    private void requireActive(){
        if (status != HoldStatus.ACTIVE){
            throw new InvalidHoldStateException(id, "expected State was Active but it is " + status);
        }
    }

    public  SeatHold Confirmed() {
        requireActive();
        return new SeatHold(
                id,
                eventId,
                customerId,
                seatIds,
                HoldStatus.CONFIRMED,
                createdAt
        );
    }

    public SeatHold cancelled() {
        requireActive();

        return new SeatHold(
                id,
                eventId,
                customerId,
                seatIds,
                HoldStatus.CANCELLED,
                createdAt
        );
    }
}
