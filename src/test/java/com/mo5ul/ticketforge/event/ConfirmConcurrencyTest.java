package com.mo5ul.ticketforge.event;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
class ConfirmConcurrencyTest{
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
    private EventSeat newSeat() {
        Event event = events.save(new Event("Test", "Venue", Instant.now().plusSeconds(86400)));
        return seats.save(new EventSeat(event.getId(), "A", 1, 50000));
    }
    private String newUser() {
        String email = UUID.randomUUID() + "@test.com";
        users.save(new User(email, "x"));
        return email;
    }
    private List<Boolean> race(List<Runnable> tasks) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(tasks.size());
        CountDownLatch ready = new CountDownLatch(tasks.size());
        CountDownLatch go = new CountDownLatch(1);
        List<Future<Boolean>> futures = new ArrayList<>();
        for (Runnable task : tasks) {
            Callable<Boolean> call = () -> {
                ready.countDown();
                go.await();
                try {
                    task.run();
                    return true;
                } catch (ResponseStatusException e) {
                    return false;
                }
            };
            futures.add(pool.submit(call));
        }
        ready.await();
        go.countDown();
        List<Boolean> results = new ArrayList<>();
        for (Future<Boolean> f : futures) results.add(f.get());
        pool.shutdown();
        return results;
    }
    private long countTrue(List<Boolean> results) {
        return results.stream().filter(b -> b).count();
    }
    @Test
    void exactlyOneOf200DuplicateConfirmsWins() throws Exception {
        EventSeat seat = newSeat();
        String holder = newUser();
        holdService.hold(seat.getEventId(), seat.getId(), holder);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            tasks.add(() -> holdService.confirm(seat.getEventId(), seat.getId(), holder));
        }
        List<Boolean> results = race(tasks);
        assertThat(countTrue(results)).isEqualTo(1);
        assertThat(seats.findById(seat.getId()).orElseThrow().getStatus())
                .isEqualTo(SeatStatus.BOOKED);
    }
    @Test
    void confirmsAndForeignHoldsRaceOnALiveHold() throws Exception {
        EventSeat seat = newSeat();
        String holder = newUser();
        holdService.hold(seat.getEventId(), seat.getId(), holder);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            tasks.add(() -> holdService.confirm(seat.getEventId(), seat.getId(), holder));
        }
        for (int i = 0; i < 100; i++) {
            String other = newUser();
            tasks.add(() -> holdService.hold(seat.getEventId(), seat.getId(), other));
        }
        List<Boolean> results = race(tasks);
        assertThat(countTrue(results.subList(0, 100))).isEqualTo(1);
        assertThat(countTrue(results.subList(100, 200))).isEqualTo(0); 
        assertThat(seats.findById(seat.getId()).orElseThrow().getStatus())
                .isEqualTo(SeatStatus.BOOKED);
    }
    @Test
    void exactlyOneHoldWinsAnExpiredSeatAndLateConfirmsFail() throws Exception {
        EventSeat seat = newSeat();
        String oldHolder = newUser();
        Long oldHolderId = users.findByEmail(oldHolder).orElseThrow().getId();
        EventSeat stale = seats.findById(seat.getId()).orElseThrow();
        stale.hold(oldHolderId, Instant.now().minusSeconds(60)); 
        seats.save(stale);
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            String challenger = newUser();
            tasks.add(() -> holdService.hold(seat.getEventId(), seat.getId(), challenger));
        }
        for (int i = 0; i < 20; i++) {
            tasks.add(() -> holdService.confirm(seat.getEventId(), seat.getId(), oldHolder));
        }
        List<Boolean> results = race(tasks);
        assertThat(countTrue(results.subList(0, 200))).isEqualTo(1);    
        assertThat(countTrue(results.subList(200, 220))).isEqualTo(0);  
        assertThat(seats.findById(seat.getId()).orElseThrow().getStatus())
                .isEqualTo(SeatStatus.HELD);
    }
}