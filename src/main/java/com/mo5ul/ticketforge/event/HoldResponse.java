package com.mo5ul.ticketforge.event;
import java.time.Instant;
public record HoldResponse(Long seatId, Instant holdExpiresAt){}