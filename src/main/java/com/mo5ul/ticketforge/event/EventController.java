package com.mo5ul.ticketforge.event;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/events")
public class EventController{
    private final EventService service;
    public EventController(EventService service){
        this.service = service;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@Valid @RequestBody CreateEventRequest req){
        return service.create(req);
    }
    @GetMapping
    public List<EventResponse> list(){
        return service.list();
    }
    @GetMapping("/{id}/seats")
    public List<SeatResponse> seats(@PathVariable Long id){
        return service.seatsFor(id);
    }
}