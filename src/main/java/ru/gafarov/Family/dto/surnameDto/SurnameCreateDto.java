package ru.gafarov.Family.dto.surnameDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class SurnameCreateDto {

    @Min(value = 1, message = "id cannot be less 1")
    private Long id;

    @Pattern(regexp = "[A-ZА-Я][A-ZА-Яа-яa-z\\-]+", message = "surname is not valid")
    @Size(min = 2, message = "surname must be more two symbol")
    private String surname;


    @Pattern(regexp = "[A-ZА-Я][A-ZА-Яа-яa-z\\-]+", message = "surAlias1 is not valid")
    @Size(min = 2, message = "surname must be more two symbol")
    private String surnameAlias1;

    @Pattern(regexp = "[A-ZА-Я][A-ZА-Яа-яa-z\\-]+", message = "surAlias2 is not valid")
    @Size(min = 2, message = "surname must be more two symbol")
    private String surnameAlias2;

    @Pattern(regexp = "[YN]", message = "declension may be Y or N")
    private String declension;

    @Min(value = 1, message = "author_id cannot be less 1")
    private Long author_id;

    private String status;
}
