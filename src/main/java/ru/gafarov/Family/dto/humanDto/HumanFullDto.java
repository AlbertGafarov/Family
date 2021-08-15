package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@JsonIgnoreProperties
@SuperBuilder
public class HumanFullDto {

    Long id;

    protected String name;

    protected String patronim;

    protected String surname;

    protected String birthdate;

    protected String deathdate;

    protected String birthplace;

    protected char gender;

    protected List<HumanShortDto> parents;

    protected List<HumanShortDto> children;
}
