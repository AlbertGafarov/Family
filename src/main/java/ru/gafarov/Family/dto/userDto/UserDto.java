package ru.gafarov.Family.dto.userDto;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@SuperBuilder
public class UserDto implements Comparable<UserDto> {

    private final Long id;
    private final String username;
    private final long phone;
    private final String email;

    @Override
    public int compareTo(UserDto anotherUser) {
        return this.id.compareTo(anotherUser.id);
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phone=" + phone +
                ", email='" + email + '\'' +
                '}';
    }
}