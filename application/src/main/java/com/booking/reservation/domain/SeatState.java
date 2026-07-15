package com.booking.reservation.domain;

import com.booking.reservation.exception.InvalidHoldStateException;
import com.booking.reservation.exception.SeatUnavailableException;

import java.util.Objects;

public record SeatState(
        EventId eventId,
        SeatId seatId,
        SeatStatus status,
        HoldId holdId,
        CustomerId owner
        )
{
        public SeatState {
            Objects.requireNonNull(eventId, "Event Id cannot be null");
            Objects.requireNonNull(seatId, "Seat Id cannot be null");
            Objects.requireNonNull(status, "Seat Status cannot be null");
            if (status == SeatStatus.AVAILABLE) {
                if (holdId != null || owner != null) {
                    throw new IllegalArgumentException("An available seat cannot be held or have an owner");
                }
            }
            if (status != SeatStatus.AVAILABLE) {
                if (holdId != null || owner != null) {
                    throw new IllegalArgumentException("An held seat must have hold Id or owner");
                }
            }
        }

        public static SeatState available(EventId eventId, SeatId seatId) {
            return new SeatState(eventId, seatId, SeatStatus.AVAILABLE, null, null);
        }

        public SeatState hold(HoldId newHoldId, CustomerId customerId) {
            Objects.requireNonNull(holdId, "Hold Id cannot be null");
            Objects.requireNonNull(customerId, "Customer Id cannot be null");
            if (status != SeatStatus.AVAILABLE) {
                throw new SeatUnavailableException(seatId);
            }
            return new SeatState(
                    eventId,
                    seatId,
                    SeatStatus.HELD,
                    newHoldId,
                    customerId
            );
        }

        public boolean isHeldBy(HoldId expectedHoldId, CustomerId expectedCustomerId) {
            return status == SeatStatus.HELD && holdId.equals(expectedHoldId) && owner.equals(expectedCustomerId);
        }

        private void validateActiveHold( HoldId expectedHoldId, CustomerId expecteedCustomerId) {
            if (!(isHeldBy(expectedHoldId,expecteedCustomerId))) {
                throw new InvalidHoldStateException(
                        expectedHoldId,
                        "seat " + seatId.value()
                                + " is not held by the expected customer"
                );
            }
        }

        public SeatState book (HoldId expectedHoldId, CustomerId expectedCustomerId) {
          validateActiveHold(expectedHoldId, expectedCustomerId);
          return new SeatState(
                  eventId,
                  seatId,
                  SeatStatus.BOOKED,
                  expectedHoldId,
                  expectedCustomerId
          );
        }

        public SeatState release(HoldId expectedHoldId, CustomerId expectedCustomerId) {
            validateActiveHold(expectedHoldId, expectedCustomerId);
            return available(eventId, seatId);
        }

        public boolean belongsTo (EventId expectedEventId) {
            return eventId.equals(expectedEventId);
        }
}
