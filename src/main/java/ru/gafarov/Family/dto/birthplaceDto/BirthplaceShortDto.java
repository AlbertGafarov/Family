package ru.gafarov.Family.dto.birthplaceDto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class BirthplaceShortDto {
    private Long id;
    private String birthplace;
}
