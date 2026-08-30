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
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
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
public class BookingPessimisticConcurrencyTest {
    @Autowired private BookingService bookingService;
    @Autowired private VenueRepository venueRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private Long seatId;
    private Long eventId;
    private List<Long> userIds;

    @BeforeEach
    void setup() {
        Venue venue = new Venue();
        venue.setName("Pessimistic Test Arena");
        venue.setCity("Hyderabad");
        venue.setAddress("Test Address");
        venue.setTotalCapacity(1);
        venue = venueRepository.save(venue);

        Seat seat = new Seat();
        seat.setRowLabel("B");
        seat.setSeatNumber(1);
        seat.setSeatType(SeatType.REGULAR);
        seat.setVenue(venue);
        seat = seatRepository.save(seat);
        seatId = seat.getId();

        Event event = new Event();
        event.setTitle("Pessimistic Concurrency Test Event");
        event.setCategory("Test");
        event.setStartTime(LocalDateTime.now().plusDays(1));
        event.setVenue(venue);
        event = eventRepository.save(event);
        eventId = event.getId();

        userIds = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).stream()
                .map(i -> {
                    User u = new User();
                    u.setEmail("pracer" + i + "@test.com");
                    u.setName("PRacer " + i);
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
    void onlyOneBookingShouldSucceedUnderPessimisticLocking() throws InterruptedException {
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
                    startLatch.await();

                    BookingRequestDto dto = new BookingRequestDto();
                    dto.setEventId(eventId);
                    dto.setSeatId(seatId);
                    dto.setUserId(userId);

                    bookingService.createBookingPessimistic(dto);
                    successCount.incrementAndGet();

                } catch (Exception ex) {
                    conflictCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await(15, TimeUnit.SECONDS);
        executor.shutdown();

        long actualBookingsForSeat = bookingRepository.findByEventId(eventId, Pageable.unpaged())
                .stream()
                .filter(b -> b.getSeat().getId().equals(seatId))
                .count();

        assertEquals(1, successCount.get(), "Exactly one booking should have succeeded");
        assertEquals(9, conflictCount.get(), "Nine bookings should have been rejected");
        assertEquals(1, actualBookingsForSeat, "Exactly one row should exist in bookings table for this seat");
    }

}
