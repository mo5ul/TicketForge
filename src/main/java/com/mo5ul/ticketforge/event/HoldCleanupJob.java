package com.mo5ul.ticketforge.event;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component 
public class HoldCleanupJob{
    private static final Logger log=LoggerFactory.getLogger(HoldCleanupJob.class);
    private final EventSeatRepository seats;
    public HoldCleanupJob(EventSeatRepository seats){
        this.seats = seats;
    }
    @Scheduled(fixedDelay=60_000)
    @Transactional
    public void releaseExpiredHolds(){
        int released = seats.releaseExpired(Instant.now());
        if(released>0){
            log.info("Released {} expired seat holds", released);
        }
    }
}