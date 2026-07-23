package com.booking.reservation.service;

import com.booking.reservation.domain.*;
import com.booking.reservation.exception.*;

import java.time.Clock;
import java.util.*;

public final class SequentialBookingEngine implements BookingEngine{

    private final Map<EventId, Set<SeatId>> eventSeats = new HashMap<>();

    private final Map<SeatId, SeatState> seats = new HashMap<>();

    private final Map<HoldId, SeatHold> holds = new HashMap<>();

    private final Map<BookingId, Booking> bookings = new HashMap<>();

    @Override
    public SeatHold holdSeats(EventId eventId, CustomerId customerId, Set<SeatId> seatIds) {
        Objects.requireNonNull(eventId, "Event Id cannot be Null");
        Objects.requireNonNull(customerId, "Customer Id cannot be Null");
        Set<SeatId> requestedSeatIds = copyAndValidateSeatIds(seatIds);
        List<SeatState> requestedSeats = requestedSeatIds.stream()
                        .map(seatId -> requireSeat(eventId, seatId))
                        .toList();
        for (SeatState seat : requestedSeats) {
            if (seat.status() != SeatStatus.AVAILABLE) {
                throw new SeatUnavailableException(seat.seatId());
            }
        }

        HoldId holdId = HoldId.random();

        for (SeatState seat : requestedSeats) {
            SeatState heldSeat = seat.hold(holdId, customerId);
            seats.put(seat.seatId(), heldSeat);
        }

        SeatHold hold = SeatHold.active(
                holdId,
                eventId,
                customerId,
                requestedSeatIds,
                Clock.systemUTC().instant()
        );

        holds.put(holdId, hold);

        return hold;
    }

    @Override
    public Booking confirmHold(HoldId holdId, CustomerId customerId) {
        Objects.requireNonNull(holdId, "Hold Id cannot be Null");
        Objects.requireNonNull(customerId, "Customer Id cannot be Null");

        SeatHold hold = requireHold(holdId);
        validateHoldOwner(hold,customerId);
        validateHoldIsActive(hold);
        List<SeatState> heldSeats = hold.seatIds().stream().map(seatId -> requireSeat(hold.eventId(),seatId)).toList();
        for (SeatState seat : heldSeats) {
            if (!seat.isHeldBy(holdId,customerId)) {
                throw new InvalidHoldStateException(
                        holdId,
                        "Seat " + seat.seatId().value()
                                + " is not held by this hold"
                );
            }
        }
        for (SeatState seat: heldSeats) {
            SeatState bookedSeat = seat.book(holdId,customerId);
            seats.put(seat.seatId(),bookedSeat);
        }
        SeatHold confirmedHold = hold.Confirmed();
        holds.put(confirmedHold.id(),confirmedHold);
        Booking booking = new Booking(
                BookingId.random(),
                holdId,
                hold.eventId(),
                customerId,
                hold.seatIds(),
                Clock.systemUTC().instant()
        );
        bookings.put(booking.id(),booking);
        return booking;
    }

    @Override
    public void cancelHold(HoldId holdId, CustomerId customerId) {
        Objects.requireNonNull(holdId, "Hold Id cannot be null");
        Objects.requireNonNull(customerId, "Customer Id cannot be null");

        SeatHold hold = requireHold(holdId);
        validateHoldOwner(hold, customerId);
        validateHoldIsActive(hold);

        List<SeatState> heldSeats = hold.seatIds().stream().map(
                seatId -> requireSeat(hold.eventId(),seatId)).toList();

        for(SeatState seatState : heldSeats) {
            if (!seatState.isHeldBy(holdId, customerId)) {
                throw new InvalidHoldStateException(
                        holdId,
                        "Seat " + seatState.seatId().value()
                                + " is not held by this hold"
                );
            }
        }

        for (SeatState seat : heldSeats) {
            SeatState availableSeat =
                    seat.release(holdId, customerId);
            seats.put(
                    seat.seatId(),
                    availableSeat
            );
        }

        SeatHold cancelledHold = hold.cancelled();
        holds.put(
                holdId,
                cancelledHold
        );
    }

    public void registerEvent(EventId eventId, Set<SeatId> seatIds) {
        Objects.requireNonNull(eventId,"Event Id must not be null");
        Set<SeatId> copiedSeatIds = copyAndValidateSeatIds(seatIds);
        if (eventSeats.containsKey(eventId)) {
            throw new IllegalArgumentException("Event is already registered" + eventId.value());
        }

        for (SeatId seatId : copiedSeatIds) {
            if (seats.containsKey(seatId)) {
                throw new IllegalArgumentException("Seat is already registered" + seatId.value());
            }
        }
        eventSeats.put(eventId, copiedSeatIds);

        for (SeatId seatId : copiedSeatIds) {
            seats.put(seatId, SeatState.available(eventId, seatId));
        }
    }

    public SeatStatus seatStatus(SeatId seatId) {
        Objects.requireNonNull(seatId, "Seat Id is not Null");
        SeatState seat = seats.get(seatId);
        if (seat == null) {
            throw new SeatNotFoundException(seatId);
        }
        return seat.status();
    }

    private Set<SeatId> copyAndValidateSeatIds(Set<SeatId> seatIds) {
        Objects.requireNonNull(seatIds, "Seat Id's must not be null");
        if (seatIds.isEmpty()) {
            throw new IllegalArgumentException("Seat Id's cannot be empty");
        }
        return Set.copyOf(seatIds);
    }


    private SeatState requireSeat(EventId eventId, SeatId seatId) {
        SeatState seat = seats.get(seatId);
        if (seat == null) {
            throw new SeatNotFoundException(seatId);
        }

        if (!seat.eventId().equals(eventId)) {
            throw new SeatNotFoundException(seatId);
        }
        return  seat;
    }

    private SeatHold requireHold(HoldId holdId) {
        SeatHold hold = holds.get(holdId);
        if (hold == null){
            throw new HoldNotFoundException(holdId);
        }
        return hold;
    }

    private void validateHoldOwner(
            SeatHold hold,
            CustomerId customerId
    ) {
        if (!hold.customerId().equals(customerId)) {
            throw new HoldOwnershipException(hold.id(), customerId);
        }
    }


    private void validateHoldIsActive(SeatHold hold) {
        if (hold.status() != HoldStatus.ACTIVE) {
            throw new InvalidHoldStateException( hold.id(), "expected ACTIVE but was " + hold.status());
        }
    }

    public HoldStatus holdStatus(HoldId holdId){
        Objects.requireNonNull(holdId, "Hold Id cannot be null");
        return requireHold(holdId).status();
    }

    public int bookingCount() {
        return bookings.size();
    }
}
