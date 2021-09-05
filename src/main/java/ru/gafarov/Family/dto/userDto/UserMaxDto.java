package ru.gafarov.Family.dto.userDto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
public class UserMaxDto extends UserDto {

    private final String[] roles;
    private String password;
    private String status;
    private Date created;
    private Date updated;

    @Override
    public String toString() {
        return "UserMaxDto{" +
                "roles=" + Arrays.toString(roles) +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                "} " + super.toString();
    }
}
