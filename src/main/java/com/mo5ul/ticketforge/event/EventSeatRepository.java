package com.mo5ul.ticketforge.event;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EventSeatRepository extends JpaRepository<EventSeat, Long>{
    List<EventSeat> findByEventIdOrderByRowLabelAscSeatNumberAsc(Long eventId);
}