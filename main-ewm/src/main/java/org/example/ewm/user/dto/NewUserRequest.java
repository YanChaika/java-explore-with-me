package org.example.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotNull
    @NotBlank
    @Size(min = 6, max = 254, message
            = "Email must be between 6 and 64 characters")
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250, message
            = "Name must be between 2 and 250 characters")
    private String name;
}
