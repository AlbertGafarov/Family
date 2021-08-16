package ru.gafarov.Family.dto.userDto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateDto {
    @Setter
    private Long id;
    @Size(min = 2, message = "username must be more two symbol")
    private String username;
    @Min(79000000000L)
    @Max(79999999999L)
    private Long phone;
    @Email
    private String email;
    private String[] roles;

    @Size(min = 3, message = "password must be more two symbol")
    private String password;

    private String status;
}
