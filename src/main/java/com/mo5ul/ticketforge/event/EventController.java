package com.mo5ul.ticketforge.event;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.mo5ul.ticketforge.payment.OrderResponse;
import com.mo5ul.ticketforge.payment.PaymentService;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;
    private final HoldService holds;
    private final PaymentService payments;
    public EventController(EventService service, HoldService holds, PaymentService payments) {
        this.service = service;
        this.holds = holds;
        this.payments = payments;
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
    public HoldResponse hold(@PathVariable Long eventId, @PathVariable Long seatId,Authentication auth) {
        return holds.hold(eventId, seatId, auth.getName());
    }
    @PostMapping("/{eventId}/seats/{seatId}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable Long eventId, @PathVariable Long seatId, Authentication auth) {
        holds.confirm(eventId, seatId, auth.getName());
    }
    @PostMapping("/{eventId}/seats/{seatId}/order")
    public OrderResponse createOrder(@PathVariable Long eventId, @PathVariable Long seatId,Authentication auth) {
        return payments.createOrder(eventId, seatId, auth.getName());
    }
}