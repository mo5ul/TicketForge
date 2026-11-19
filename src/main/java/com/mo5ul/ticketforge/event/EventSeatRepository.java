package com.mo5ul.ticketforge.event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
    List<EventSeat> findByEventIdOrderByRowLabelAscSeatNumberAsc(Long eventId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from EventSeat s where s.id = :seatId and s.eventId = :eventId")
    Optional<EventSeat> findForUpdate(@Param("eventId") Long eventId, @Param("seatId") Long seatId);
}