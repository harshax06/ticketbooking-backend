package com.harsha.ticketbooking;

import com.harsha.ticketbooking.dto.request.BookingRequestDto;
import com.harsha.ticketbooking.entity.*;
import com.harsha.ticketbooking.repository.*;
import com.harsha.ticketbooking.service.BookingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("dev")
public class BookingConcurrencyTest {
    @Autowired private BookingService bookingService ;
    @Autowired private VenueRepository venueRepository ;
    @Autowired private SeatRepository seatRepository ;
    @Autowired private EventRepository eventRepository ;
    @Autowired private UserRepository userRepository ;
    @Autowired private BookingRepository bookingRepository ;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Long seatId ;
    private Long eventId ;
    private List<Long> userIds ;

    @BeforeEach
    void setUp() {
        Venue venue = new Venue() ;
        venue.setName("Test Arena");
        venue.setCity("Hyderabad");
        venue.setAddress("Test Address");
        venue.setTotalCapacity(1);
        venue = venueRepository.save(venue);

        Seat seat = new Seat();
        seat.setRowLabel("A");
        seat.setSeatNumber(1);
        seat.setSeatType(SeatType.REGULAR);
        seat.setVenue(venue);
        seat = seatRepository.save(seat);
        seatId = seat.getId();

        Event event = new Event();
        event.setTitle("Concurrency Test Event");
        event.setCategory("Test");
        event.setStartTime(LocalDateTime.now().plusDays(1));
        event.setVenue(venue);
        event = eventRepository.save(event);
        eventId = event.getId();

        userIds = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).stream()
                .map(i -> {
                    User u = new User();
                    u.setEmail("racer" + i + "@test.com");
                    u.setName("Racer " + i);
                    u.setPasswordHash("test-password-hash");
                    return userRepository.save(u).getId();
                })
                .toList();
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("""
            TRUNCATE TABLE refresh_tokens, bookings, seats, events, users, venues
            RESTART IDENTITY CASCADE
            """);
    }

    @Test
    void onlyOneBookingShouldSucceedWhenMultipleUsersRaceForSameSeat() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        for (Long userId : userIds) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();  // all threads block here until released together

                    BookingRequestDto dto = new BookingRequestDto();
                    dto.setEventId(eventId);
                    dto.setSeatId(seatId);
                    dto.setUserId(userId);

                    bookingService.createBooking(dto);
                    successCount.incrementAndGet();

                } catch (Exception ex) {
                    // Any conflict exception (409-mapped) counts as expected rejection
                    conflictCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();              // wait until all 10 threads are ready
        startLatch.countDown();           // release them all at (almost) exactly the same instant
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // The real assertion: exactly ONE booking exists for this seat+event,
        // no matter how many threads raced for it.
        long actualBookingsForSeat = bookingRepository.findByEventId(eventId, org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .filter(b -> b.getSeat().getId().equals(seatId))
                .count();

        assertEquals(1, successCount.get(), "Exactly one booking should have succeeded");
        assertEquals(9, conflictCount.get(), "Nine bookings should have been rejected");
        assertEquals(1, actualBookingsForSeat, "Exactly one row should exist in bookings table for this seat");
    }

}
