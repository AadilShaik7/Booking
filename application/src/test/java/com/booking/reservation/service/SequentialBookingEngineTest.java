package com.booking.reservation.service;

import com.booking.reservation.domain.CustomerId;
import com.booking.reservation.domain.EventId;
import com.booking.reservation.domain.SeatId;
import com.booking.reservation.domain.SeatStatus;
import com.booking.reservation.exception.SeatNotFoundException;
import com.booking.reservation.exception.SeatUnavailableException;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SequentialBookingEngineTest {


    @Test
    void registerEventCreatesAvailableSeats() {
        SequentialBookingEngine engine = new SequentialBookingEngine();
        EventId eventId = new EventId(UUID.randomUUID());

        SeatId seatOne = new SeatId(UUID.randomUUID());
        SeatId seatTwo = new SeatId(UUID.randomUUID());
        SeatId seatThree = new SeatId(UUID.randomUUID());

        engine.registerEvent(
                eventId,
                Set.of(seatOne, seatTwo, seatThree)
        );

        assertEquals(
                SeatStatus.AVAILABLE,
                engine.seatStatus(seatOne)
        );

        assertEquals(
                SeatStatus.AVAILABLE,
                engine.seatStatus(seatTwo)
        );

        assertEquals(
                SeatStatus.AVAILABLE,
                engine.seatStatus(seatThree)
        );
    }


    @Test
    void sameEventCannotBeRegisteredTwice() {
        SequentialBookingEngine engine =
                new SequentialBookingEngine();

        EventId eventId = EventId.random();

        SeatId originalSeat = SeatId.random();
        SeatId secondSeat = SeatId.random();

        engine.registerEvent(
                eventId,
                Set.of(originalSeat)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> engine.registerEvent(
                        eventId,
                        Set.of(secondSeat)
                )
        );
    }

    @Test
    void seatCannotBelongToTwoEvents() {
        SequentialBookingEngine engine = new SequentialBookingEngine();
        EventId eventId =   new EventId(UUID.randomUUID());
        SeatId seatId = new SeatId(UUID.randomUUID());
        EventId eventId2 = new EventId(UUID.randomUUID());

        engine.registerEvent(eventId,Set.of(seatId));

        assertThrows(IllegalArgumentException.class, ()->engine.registerEvent(
                eventId2,
                Set.of(seatId)
        ));
    }

    @Test
    void failedRegistrationDoesNotPartiallyAddSeats() {
        SequentialBookingEngine engine =
                new SequentialBookingEngine();

        EventId eventOne = EventId.random();
        EventId eventTwo = EventId.random();

        SeatId existingSeat = SeatId.random();
        SeatId newSeatOne = SeatId.random();
        SeatId newSeatTwo = SeatId.random();

        engine.registerEvent(
                eventOne,
                Set.of(existingSeat)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> engine.registerEvent(
                        eventTwo,
                        Set.of(
                                newSeatOne,
                                newSeatTwo,
                                existingSeat
                        )
                )
        );

        assertThrows(
                SeatNotFoundException.class,
                () -> engine.seatStatus(newSeatOne)
        );

        assertThrows(
                SeatNotFoundException.class,
                () -> engine.seatStatus(newSeatTwo)
        );
    }

    @Test
    void multiSeatHoldIsAllOrNothing() {
        SequentialBookingEngine engine =
                new SequentialBookingEngine();

        EventId eventId = EventId.random();

        CustomerId customerOne = CustomerId.random();
        CustomerId customerTwo = CustomerId.random();

        SeatId seatOne = SeatId.random();
        SeatId seatTwo = SeatId.random();
        SeatId seatThree = SeatId.random();

        engine.registerEvent(
                eventId,
                Set.of(seatOne, seatTwo, seatThree)
        );

        engine.holdSeats(
                eventId,
                customerOne,
                Set.of(seatThree)
        );

        assertThrows(
                SeatUnavailableException.class,
                () -> engine.holdSeats(
                        eventId,
                        customerTwo,
                        Set.of(
                                seatOne,
                                seatTwo,
                                seatThree
                        )
                )
        );

        assertEquals(
                SeatStatus.AVAILABLE,
                engine.seatStatus(seatOne)
        );

        assertEquals(
                SeatStatus.AVAILABLE,
                engine.seatStatus(seatTwo)
        );

        assertEquals(
                SeatStatus.HELD,
                engine.seatStatus(seatThree)
        );
    }
}
