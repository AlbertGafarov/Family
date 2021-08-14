package ru.gafarov.Family.dto.userDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class UserDto implements Comparable<UserDto> {

    private Long id;
    private String username;
    private long phone;
    private String email;

    @Override
    public int compareTo(UserDto anotherUser) {
        return this.id.compareTo(anotherUser.id);
    }

}