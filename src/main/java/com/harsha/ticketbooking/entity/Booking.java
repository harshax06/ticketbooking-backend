package com.harsha.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id" , "seat_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id" , nullable = false)
    private Event event ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    private User user ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status ;

    @Column(name = "booked_at" , nullable = false)
    private LocalDateTime bookedAt ;
}
