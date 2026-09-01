package com.harsha.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlist")
@Getter
@Setter
public class Waitlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;
    @ManyToOne
    @JoinColumn(name = "user_id")

    private User user;
    private LocalDateTime joinedAt = LocalDateTime.now();
}
