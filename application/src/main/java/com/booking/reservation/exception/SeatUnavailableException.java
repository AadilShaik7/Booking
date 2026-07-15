package com.booking.reservation.exception;

import com.booking.reservation.domain.SeatId;

public class SeatUnavailableException extends ReservationException {

    public SeatUnavailableException(SeatId seatId) {
        super("Seat is not available" + seatId.value());
    }
}
