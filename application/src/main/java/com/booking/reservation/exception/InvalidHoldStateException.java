package com.booking.reservation.exception;

import com.booking.reservation.domain.HoldId;

public final class InvalidHoldStateException
        extends ReservationException {

    public InvalidHoldStateException(
            HoldId holdId,
            String reason
    ) {
        super(
                "Invalid state for hold "
                        + holdId.value()
                        + ": "
                        + reason
        );
    }
}