package org.example.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ewm.location.Location;

import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message
            = "Annotation must be between 20 and 2000 characters")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message
            = "Description must be between 20 and 7000 characters")
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    @Size(min = 3, max = 120, message
            = "Title must be between 3 and 120 characters")
    private String title;

}
