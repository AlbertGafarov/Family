package ru.gafarov.Family.dto.userDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserRegisterDto {

    private Long id;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private Long phone;
    @NonNull
    private String password;

    public UserRegisterDto(@NonNull String username, @NonNull String email, @NonNull Long phone, @NonNull String password) {
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

    public UserRegisterDto() {
    }
}