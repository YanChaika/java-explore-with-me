package org.example.ewm.event.model;

import lombok.*;
import org.example.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "text")
    private String text;
    @ManyToOne
    private Event event;
    @ManyToOne
    private User author;
    @Column(name = "created")
    private LocalDateTime created;
}
