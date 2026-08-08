package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.VenueRequestDto;
import com.harsha.ticketbooking.dto.response.VenueResponseDto;
import com.harsha.ticketbooking.entity.Venue;
import com.harsha.ticketbooking.exception.ResourceNotFoundException;
import com.harsha.ticketbooking.mapper.VenueMapper;
import com.harsha.ticketbooking.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class VenueService {
    private final VenueRepository venueRepository ;

    VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository ;
    }

    public VenueResponseDto create(VenueRequestDto dto) {
        Venue venue = VenueMapper.toEntity(dto);
        Venue saved = venueRepository.save(venue);
        return VenueMapper.toResponseDto(saved);
    }

    public List<VenueResponseDto> getAll() {
        return venueRepository.findAll()
                .stream()
                .map(VenueMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public VenueResponseDto getById(Long id) {
        Venue venue = findEntityById(id) ;
        return VenueMapper.toResponseDto(venue);
    }


    public VenueResponseDto update(Long id , VenueRequestDto dto) {
        Venue existing = findEntityById(id) ;
        existing.setName(dto.getName());
        existing.setCity(dto.getCity());
        existing.setAddress(dto.getAddress());
        existing.setTotalCapacity(dto.getTotalCapacity());
        Venue updated = venueRepository.save(existing);
        return VenueMapper.toResponseDto(updated);
    }

    public void delete(Long id) {
        Venue existing = findEntityById(id) ;
        venueRepository.delete(existing);
    }

    public Venue findEntityById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id : "+id));
    }

}
