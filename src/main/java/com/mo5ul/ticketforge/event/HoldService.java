package com.mo5ul.ticketforge.event;
import java.time.Duration;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.mo5ul.ticketforge.user.UserRepository;
@Service
public class HoldService {
    private static final Duration HOLD_DURATION = Duration.ofMinutes(10);
    private final EventSeatRepository seats;
    private final UserRepository users;
    public HoldService(EventSeatRepository seats, UserRepository users) {
        this.seats = seats;
        this.users = users;
    }
    @Transactional
    public HoldResponse hold(Long eventId, Long seatId, String email) {
        Long userId = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unknown user"))
                .getId();
        EventSeat seat = seats.findForUpdate(eventId, seatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));
        Instant now = Instant.now();
        if (!seat.isHoldable(now)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat is not available");
        }
        Instant expiresAt = now.plus(HOLD_DURATION);
        seat.hold(userId, expiresAt);
        return new HoldResponse(seat.getId(), expiresAt);
    }
}