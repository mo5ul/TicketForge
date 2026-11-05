package com.mo5ul.ticketforge.event;
import java.time.Instant;
import jakarta.validation.constraints.*;
public record CreateEventRequest(
    @NotBlank @Size(max=200) String name,
    @NotBlank @Size(max=200) String venue,
    @NotNull @Future Instant startsAt,
    @Min(1) @Max(26) int rows,
    @Min(1) @Max(50) int seatsPerRow,
    @Min(1) long pricePaise
){}