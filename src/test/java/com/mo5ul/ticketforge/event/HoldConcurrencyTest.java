package com.mo5ul.ticketforge.event;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import com.mo5ul.ticketforge.user.User;
import com.mo5ul.ticketforge.user.UserRepository;
@SpringBootTest 
@Testcontainers 
class HoldConcurrencyTest{
    @Container 
    @ServiceConnection 
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");
    @DynamicPropertySource 
    static void props(DynamicPropertyRegistry registry){
        registry.add("app.jwt.secret",()->"dGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQtdGVzdC1zZWNyZXQ=");
    }
    @Autowired HoldService holdService;
    @Autowired EventRepository events;
    @Autowired EventSeatRepository seats;
    @Autowired UserRepository users;
    @Test
    void exactlyOneOf200ConcurrentRequestsWinsTheSeat() throws Exception {
        int threads = 200;
        Event event = events.save(new Event("Test", "Venue", Instant.now().plusSeconds(86400)));
        EventSeat seat = seats.save(new EventSeat(event.getId(), "A", 1, 50000));
        List<String> emails = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            String email = "user" + i + "@test.com";
            users.save(new User(email, "x"));
            emails.add(email);
        }
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch go = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();
        List<Future<?>> futures = new ArrayList<>();
        for (String email : emails) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                try {
                    go.await();
                    holdService.hold(event.getId(), seat.getId(), email);
                    success.incrementAndGet();
                } catch (ResponseStatusException e) {
                    conflict.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }
        ready.await();   
        go.countDown();
        for (Future<?> f : futures) f.get();
        pool.shutdown();
        EventSeat after = seats.findById(seat.getId()).orElseThrow();
        assertThat(success.get()).isEqualTo(1);
        assertThat(conflict.get()).isEqualTo(threads - 1);
        assertThat(after.getStatus()).isEqualTo(SeatStatus.HELD);
    }
}