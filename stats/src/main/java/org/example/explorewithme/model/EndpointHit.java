package org.example.explorewithme.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity//(name = "EndpointHit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "endpoint_hit")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String app;
    private String uri;
    private String ip;
    @Column(name = "times_tamp")
    private LocalDateTime timestamp;
}
