package ru.gafarov.Family.dto.userDto;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserCreateDto {

    @Setter
    @Min(value = 1, message = "author_id cannot be less 1")
    private Long id;

    @Size(min = 2, message = "username must be more two symbol")
    @Pattern(regexp = "[A-Za-z][A-Za-z0-9@\\.]*")
    private String username;

    @Min(79000000000L)
    @Max(79999999999L)
    private Long phone;

    @Email
    private String email;
    private String[] roles;

    @Size(min = 3, message = "password must be more two symbol")
    @Pattern(regexp = "[^\\s]")
    private String password;

    private String status;
}
