package com.harsha.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.SQLDelete(sql = "UPDATE events SET deleted_at = now() WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("deleted_at IS NULL")
public class Event extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String title ;
    private String category ;
    private LocalDateTime startTime ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue ;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt ;

    public boolean isDeleted() {
        return deletedAt != null ;
    }
}
