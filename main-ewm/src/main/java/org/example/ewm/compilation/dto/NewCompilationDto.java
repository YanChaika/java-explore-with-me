package org.example.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private List<Long> events;
    private boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50, message
            = "Annotation must be between 1 and 50 characters")
    private String title;
}
