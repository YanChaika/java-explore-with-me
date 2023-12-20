package org.example.ewm.event.model;

import lombok.*;
import org.example.ewm.categories.model.Category;
import org.example.ewm.enums.State;
import org.example.ewm.location.Location;
import org.example.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "annotation")
    private String annotation;
    @ManyToOne
    private Category category;
    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @ManyToOne
    private User initiator;
    @Column(name = "paid")
    private boolean paid;
    @Column(name = "title")
    private String title;
    @Column(name = "views")
    private long views;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @Column(name = "description")
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;
    @Column(name = "participant_limit")
    private long participantLimit;
    @Column(name = "participant_count")
    private Long participantCount;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private boolean requestModeration;
    @Column(name = "state")
    @Enumerated
    private State state;
}
