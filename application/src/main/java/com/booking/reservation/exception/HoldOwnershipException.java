package com.booking.reservation.exception;

import com.booking.reservation.domain.CustomerId;
import com.booking.reservation.domain.HoldId;

public final class HoldOwnershipException
        extends ReservationException {

    public HoldOwnershipException(
            HoldId holdId,
            CustomerId customerId
    ) {
        super(
                "Customer " + customerId.value()
                        + " does not own hold "
                        + holdId.value()
        );
    }
}