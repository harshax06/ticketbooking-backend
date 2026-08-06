package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.entity.Venue;
import com.harsha.ticketbooking.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class VenueService {
    private VenueRepository venueRepository ;

    VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository ;
    }

    public Venue create(Venue venue) {
        return venueRepository.save(venue) ;
    }

    public List<Venue> getAll() {
        return venueRepository.findAll() ;
    }

    public Venue getById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Venue is not found with id : " + id)) ;
    }

    public Venue update(Long id , Venue updatedVenue) {
        Venue existing = getById(id) ;
        existing.setName(updatedVenue.getName());
        existing.setCity(updatedVenue.getCity());
        existing.setAddress(updatedVenue.getAddress());
        existing.setTotalCapacity(updatedVenue.getTotalCapacity());
        return venueRepository.save(existing) ;
    }

    public void delete(Long id) {
        Venue existing = getById(id) ;
        venueRepository.delete(existing);
    }

}
