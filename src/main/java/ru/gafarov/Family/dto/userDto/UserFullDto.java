package ru.gafarov.Family.dto.userDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class UserFullDto extends UserDto{

    private final String[] roles;

    @Override
    public String toString() {
        return "UserFullDto{" +
                "roles=" + Arrays.toString(roles) +
                "} parent:" + super.toString();
    }
}
