package com.mo5ul.ticketforge.event;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;
    private final HoldService holds;
    public EventController(EventService service, HoldService holds) {
        this.service = service;
        this.holds = holds;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@Valid @RequestBody CreateEventRequest req) {
        return service.create(req);
    }
    @GetMapping
    public List<EventResponse> list() {
        return service.list();
    }
    @GetMapping("/{id}/seats")
    public List<SeatResponse> seats(@PathVariable Long id) {
        return service.seatsFor(id);
    }
    @PostMapping("/{eventId}/seats/{seatId}/hold")
    public HoldResponse hold(@PathVariable Long eventId, @PathVariable Long seatId,
                             Authentication auth) {
        return holds.hold(eventId, seatId, auth.getName());
    }
    @PostMapping("/{eventId}/seats/{seatId}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable Long eventId, @PathVariable Long seatId, Authentication auth){
        holds.confirm(eventId, seatId, auth.getName());
    }
    
}