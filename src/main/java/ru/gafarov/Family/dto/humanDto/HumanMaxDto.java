package ru.gafarov.Family.dto.humanDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.gafarov.Family.dto.userDto.UserDto;
import ru.gafarov.Family.model.Status;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class HumanMaxDto extends HumanFullDto {

    private UserDto author;

    private Date created;

    private Date updated;

    private Status status;
}
