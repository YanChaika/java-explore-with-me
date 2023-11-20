package org.example.explorewithme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    private long id;
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
