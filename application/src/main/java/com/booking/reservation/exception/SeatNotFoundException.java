package com.booking.reservation.exception;

import com.booking.reservation.domain.SeatId;

public class SeatNotFoundException extends ReservationException {

    public SeatNotFoundException(SeatId seatId) {
        super("Seat Not Found" + seatId.value());
    }
}
