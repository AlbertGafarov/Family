package ru.gafarov.Family.dto.userDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserRegisterDto {

    private Long id;

    @NotBlank(message = "username must be not empty")
    @Size(min = 2, message = "username must be more one symbol")
    private String username;

    @Email
    @NotBlank(message = "email must be not empty")
    private String email;

    @Min(79000000000L)
    @Max(79999999999L)
    @NotNull(message = "phone is required")
    private Long phone;

    @NotBlank(message = "password must be not empty")
    @Size(min = 3, message = "password must be more two symbol")
    private String password;

    String[] roles;

    public UserRegisterDto(String username, String email, Long phone, String password) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserRegisterDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone=" + phone +
                ", password='" + password + '\'' +
                '}';
    }

}