package com.mo5ul.ticketforge.event;
import java.time.Instant;
import jakarta.persistence.*;
@Entity
@Table(name="event_seats")
public class EventSeat{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="event_id",nullable=false)
    private Long eventId;
    @Column(name="row_label",nullable=false)
    private String rowLabel;
    @Column(name="seat_number",nullable=false)
    private int seatNumber;
    @Column(name="price_paise",nullable=false)
    private long pricePaise;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private SeatStatus status = SeatStatus.AVAILABLE;
    @Column(name="held_by")
    private Long heldBy;
    @Column(name="hold_expires_at")
    private Instant holdExpiresAt;
    @Version
    private Long version;
    protected EventSeat(){}
    public EventSeat(Long eventId, String rowLabel, int seatNumber, long pricePaise){
        this.eventId=eventId;
        this.rowLabel=rowLabel;
        this.seatNumber=seatNumber;
        this.pricePaise=pricePaise;
    }
    public Long getId() {return id;}
    public Long getEventId(){return eventId;}
    public String getRowLabel(){return rowLabel;}
    public int getSeatNumber(){return seatNumber;}
    public long getPricePaise() {return pricePaise;}
    public SeatStatus getStatus() {return status;}
    public Long getHeldBy() {return heldBy;}
    public Instant getHoldExpiresAt() {return holdExpiresAt;}
}