package ru.gafarov.Family.dto.userDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
public class UserTokenDto {
    private String username;
    private String token;

    public UserTokenDto(){}
}
