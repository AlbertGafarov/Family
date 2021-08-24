package ru.gafarov.Family.dto.surnameDto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class SurnameDto {

    private Long id;

    private String surname;

    private String surnameAlias1;

    private String surnameAlias2;

    private String declension;
}
