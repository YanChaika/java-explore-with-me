package org.example.ewm.request.model;

import lombok.*;
import org.example.ewm.enums.RequestStatus;
import org.example.ewm.event.model.Event;
import org.example.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "requests")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "created")
    private LocalDateTime created;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User requester;
    @Enumerated
    private RequestStatus status;
}
