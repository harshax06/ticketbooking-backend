package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.BookingRequestDto;
import com.harsha.ticketbooking.dto.response.BookingResponseDto;
import com.harsha.ticketbooking.entity.*;
import com.harsha.ticketbooking.exception.ResourceNotFoundException;
import com.harsha.ticketbooking.exception.SeatUnavailableException;
import com.harsha.ticketbooking.mapper.BookingMapper;
import com.harsha.ticketbooking.repository.BookingRepository;
import com.harsha.ticketbooking.repository.EventRepository;
import com.harsha.ticketbooking.repository.SeatRepository;
import com.harsha.ticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository ;
    private final SeatRepository seatRepository ;
    private final UserRepository userRepository ;

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto dto) {

        log.info("Booking attempt: eventId={}, seatId={}, userId={}", dto.getEventId(), dto.getSeatId(), dto.getUserId());

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id : " + dto.getEventId())) ;
        Seat seat = seatRepository.findById(dto.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id : " + dto.getSeatId())) ;
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + dto.getUserId())) ;

        boolean alreadyBooked = bookingRepository.existsByEventIdAndSeatId(event.getId() , seat.getId()) ;
        if(alreadyBooked) {
            throw new SeatUnavailableException(
                    "Seat" + seat.getRowLabel() + seat.getSeatNumber() + " is already booked for this event");
        }

        seat.setLastTouchedAt(LocalDateTime.now());
        seatRepository.saveAndFlush(seat) ;

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setSeat(seat);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking) ;

        log.info("Booking confirmed: bookingId={}, seatId={}", saved.getId(), seat.getId());
        return BookingMapper.toResponseDto(saved) ;
    }

    @Transactional
    public BookingResponseDto createBookingPessimistic(BookingRequestDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id : " + dto.getEventId())) ;

        Seat seat = seatRepository.findByIdForUpdate(dto.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with id : " + dto.getSeatId())) ;

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User is not found with id : " + dto.getUserId())) ;

        boolean alreadyBooked = bookingRepository.existsByEventIdAndSeatId(event.getId() , seat.getId()) ;
        if(alreadyBooked) {
            throw new SeatUnavailableException(
                    "Seat" + seat.getRowLabel() + seat.getSeatNumber() + " is already booked for this event."
            ) ;
        }

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setSeat(seat);
        booking.setUser(user);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setBookedAt(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking) ;
        return BookingMapper.toResponseDto(saved) ;

    }

}
