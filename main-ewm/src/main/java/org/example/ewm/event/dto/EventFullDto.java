package org.example.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.ewm.categories.dto.CategoryDto;
import org.example.ewm.location.Location;
import org.example.ewm.user.dto.UserShortDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private long id;
    private UserShortDto initiator;
    private Location location;
    private boolean paid;
    private long participantLimit;
    private String publishedOn;
    private boolean requestModeration;
    private String state;
    private String title;
    private long views;
}
