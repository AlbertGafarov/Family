package ru.gafarov.Family.dto.birthplaceDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class BirthplaceDto extends BirthplaceShortDto {
    private UUID guid;
}
