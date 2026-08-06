package com.harsha.ticketbooking.repository;

import com.harsha.ticketbooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue,Long> {
}
