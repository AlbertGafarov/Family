package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties
public class HumanFullDto {

    private Long id;

    private String name;

    private String patronim;

    private String surname;

    private Date birthdate;

    private Date deathdate;

    private String birthplace;

    private char gender;

    private List<HumanShortDto> parents;

    private List<HumanShortDto> children;
}
