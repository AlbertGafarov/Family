package ru.gafarov.Family.dto.userDto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

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

}