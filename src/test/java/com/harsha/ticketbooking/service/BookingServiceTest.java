package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.BookingRequestDto;
import com.harsha.ticketbooking.entity.Booking;
import com.harsha.ticketbooking.entity.Event;
import com.harsha.ticketbooking.entity.Seat;
import com.harsha.ticketbooking.entity.User;
import com.harsha.ticketbooking.exception.SeatUnavailableException;
import com.harsha.ticketbooking.repository.BookingRepository;
import com.harsha.ticketbooking.repository.EventRepository;
import com.harsha.ticketbooking.repository.SeatRepository;
import com.harsha.ticketbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private EventRepository eventRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private UserRepository userRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDto dto;
    private Event event;
    private Seat seat;
    private User user;

    @BeforeEach
    void setup() {
        dto = new BookingRequestDto();
        dto.setEventId(1L);
        dto.setSeatId(1L);
        dto.setUserId(1L);

        event = new Event();
        event.setId(1L);

        seat = new Seat();
        seat.setId(1L);
        seat.setRowLabel("A");
        seat.setSeatNumber(1);

        user = new User();
        user.setId(1L);
    }

    @Test
    void throwsSeatUnavailableWhenAlreadyBooked() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByEventIdAndSeatId(1L, 1L)).thenReturn(true);

        assertThrows(SeatUnavailableException.class, () -> bookingService.createBooking(dto));

        // Verify we never even attempted the insert once we knew the seat was taken
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void savesBookingWhenSeatIsAvailable() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByEventIdAndSeatId(1L, 1L)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        bookingService.createBooking(dto);

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

}
