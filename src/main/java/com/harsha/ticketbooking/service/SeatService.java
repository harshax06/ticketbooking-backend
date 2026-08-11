package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.SeatRequestDto;
import com.harsha.ticketbooking.dto.response.SeatResponseDto;
import com.harsha.ticketbooking.entity.Seat;
import com.harsha.ticketbooking.entity.Venue;
import com.harsha.ticketbooking.mapper.SeatMapper;
import com.harsha.ticketbooking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository ;
    private final VenueService venueService ;

    @Transactional
    public List<SeatResponseDto> createBulk(Long venueId , List<SeatRequestDto> seatDtos) {
        Venue venue = venueService.findEntityById(venueId);
        List<Seat> seats = seatDtos.stream()
                .map(dto -> SeatMapper.toEntity(dto,venue))
                .collect(Collectors.toList());
        List<Seat> saved = seatRepository.saveAll(seats);
        return saved.stream().map(SeatMapper::toResponseDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SeatResponseDto> getByVenue(Long venueId , Pageable pageable) {
        return seatRepository.findByVenueId(venueId , pageable)
                .map(SeatMapper::toResponseDto);
    }
}
