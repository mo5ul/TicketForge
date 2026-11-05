package com.mo5ul.ticketforge.event;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class EventService{
    private final EventRepository events;
    private final EventSeatRepository seats;
    public EventService(EventRepository events, EventSeatRepository seats){
        this.events = events;
        this.seats = seats;
    }
    @Transactional
    public EventResponse create(CreateEventRequest req){
        Event event = events.save(new Event(req.name(), req.venue(), req.startsAt()));
        List<EventSeat> grid = new ArrayList<>();
        for (int r = 0; r < req.rows(); r++){
            String rowLabel = String.valueOf((char) ('A' + r));
            for (int n = 1; n <= req.seatsPerRow(); n++){
                grid.add(new EventSeat(event.getId(), rowLabel, n, req.pricePaise()));
            }
        }
        seats.saveAll(grid);
        return EventResponse.from(event);
    }
    public List<EventResponse> list(){
        return events.findAll().stream().map(EventResponse::from).toList();
    }
    public List<SeatResponse> seatsFor(Long eventId){
        if (!events.existsById(eventId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }
        return seats.findByEventIdOrderByRowLabelAscSeatNumberAsc(eventId).stream().map(SeatResponse::from).toList();
    }
}