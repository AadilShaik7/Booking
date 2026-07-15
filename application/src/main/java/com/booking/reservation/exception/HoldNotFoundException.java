package com.booking.reservation.exception;

import com.booking.reservation.domain.HoldId;

public class HoldNotFoundException extends ReservationException {

    public HoldNotFoundException(HoldId holdId) {
        super("Hold not found" + holdId.value());
    }
}

