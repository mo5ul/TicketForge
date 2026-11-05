package com.mo5ul.ticketforge.event;
public record SeatResponse(Long id, String rowLabel, int seatNumber,long pricePaise, SeatStatus status){
    public static SeatResponse from(EventSeat s){
        return new SeatResponse(s.getId(), s.getRowLabel(), s.getSeatNumber(),s.getPricePaise(), s.getStatus());
    }
}