package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Calendar;

@Data
@JsonIgnoreProperties
public class HumanCreateDto {

    @Min(value = 1, message = "id cannot be less 1")
    private Long id;

    @Pattern(regexp = "[A-ZА-Я][A-ZА-Яа-яa-z\\-]+", message = "surname is not valid")
    @Size(min = 2, message = "name must be more two symbol")
    private String name;

    @Pattern(regexp = "[A-ZА-Я][A-ZА-Яа-яa-z\\-]+", message = "patronim is not valid")
    @Size(min = 2, message = "patronim must be more two symbol")
    private String patronim;

    @Min(value = 1, message = "surname_id cannot be less 1")
    private Long surname_id;

    @PastOrPresent
    private Calendar birthdate;

    @PastOrPresent
    private Calendar deathdate;

    @Min(value = 1, message = "birthplace_id cannot be less 1")
    private Long birthplace_id;

    @NotNull
    @Pattern(regexp = "[MW]", message = "gender may be M or W")
    private String gender;

    private int[] parents_id;

    private int[] children_id;

    private int[] previous_surnames_id;

    @Min(value = 1, message = "author_id cannot be less 1")
    private Long author_id;

    private String status;
}
