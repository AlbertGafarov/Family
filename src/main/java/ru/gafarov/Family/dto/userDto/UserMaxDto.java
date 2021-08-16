package ru.gafarov.Family.dto.userDto;

import lombok.experimental.SuperBuilder;

import java.util.Date;

@SuperBuilder
public class UserMaxDto extends UserFullDto {

    private final String password;
    private final String status;
    private final Date created;
    private final Date updated;
}
