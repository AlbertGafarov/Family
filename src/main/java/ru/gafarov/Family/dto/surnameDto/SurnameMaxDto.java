package ru.gafarov.Family.dto.surnameDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.gafarov.Family.dto.userDto.UserDto;
import ru.gafarov.Family.model.Status;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class SurnameMaxDto extends SurnameDto {

    private UserDto author;
    private Date created;
    private Date updated;
    private Status status;

}
