package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@JsonIgnoreProperties
@SuperBuilder
public class HumanShortDto {

    protected Long id;
    protected String surname;
    protected String name;
    protected String patronim;
}
