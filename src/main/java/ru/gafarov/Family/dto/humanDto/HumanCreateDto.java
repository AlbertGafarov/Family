package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Calendar;

@Data
@JsonIgnoreProperties
public class HumanCreateDto {
    @Min(1)
    private Long id;

    @NotNull
    @Size(min = 2, message = "name must be more two symbol")
    private String name;

    @NotBlank()
    @Size(min = 2, message = "patronim must be more two symbol")
    private String patronim;

    private Long surname_id;

    private Calendar birthdate;

    private Calendar deathdate;

    @Min(1)
    private Long birthplace_id;

    @NotNull
    private Character gender;

    private int[] parents_id;


    private int[] children_id;


    private int[] previous_surnames_id;

    @Min(1)
    private Long author_id;
}
