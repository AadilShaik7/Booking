package com.booking.reservation.service;

import com.booking.reservation.domain.*;

import java.util.Set;

public interface BookingEngine {

    SeatHold holdSeats(
            EventId eventId,
            CustomerId customerId,
            Set<SeatId> seatIds
    );

    Booking confirmHold(HoldId holdId, CustomerId customerId);
    void cancelHold(HoldId holdId, CustomerId customerId);
}
