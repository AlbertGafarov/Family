package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Calendar;

@Data
@JsonIgnoreProperties
public class HumanCreateDto {

    private Long id;

    private String name;

    private String patronim;

    private Long surname_id;

    private Calendar birthdate;

    private Calendar deathdate;

    private Long birthplace_id;

    private Character gender;

    private int[] parents_id;

    private int[] children_id;

    private int[] previous_surnames_id;
}
