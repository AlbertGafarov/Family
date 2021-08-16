package ru.gafarov.Family.dto.userDto;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class UserFullDto extends UserDto{

    private final String[] roles;

}
