package com.mo5ul.ticketforge.event;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import org.springframework.data.jpa.repository.Modifying;
public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {
    List<EventSeat> findByEventIdOrderByRowLabelAscSeatNumberAsc(Long eventId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from EventSeat s where s.id = :seatId and s.eventId = :eventId")
    Optional<EventSeat> findForUpdate(@Param("eventId") Long eventId, @Param("seatId") Long seatId);
    @Modifying 
    @Query("""
        update EventSeat s
            set s.status=com.mo5ul.ticketforge.event.SeatStatus.AVAILABLE,
                s.heldBy=null,
                s.holdExpiresAt=null
        where s.status=com.mo5ul.ticketforge.event.SeatStatus.HELD and s.holdExpiresAt < :now""")
    int releaseExpired(@Param("now")Instant now);
}