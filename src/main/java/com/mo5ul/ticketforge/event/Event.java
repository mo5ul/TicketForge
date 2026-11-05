package com.mo5ul.ticketforge.event;
import java.time.Instant;
import jakarta.persistence.*;
@Entity 
@Table(name="events")
public class Event{
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false)
    private String name;
    @Column(nullable=false)
    private String venue;
    @Column(name="starts_at",nullable=false)private Instant startsAt;
    protected Event(){}
    public Event(String name,String venue, Instant startsAt){
        this.name=name;
        this.venue=venue;
        this.startsAt=startsAt;
    }
    public Long getId(){return id;}
    public String getName(){return name;}
    public String getVenue(){return venue;}
    public Instant getStartsAt(){return startsAt;}
}
