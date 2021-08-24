package ru.gafarov.Family.dto.birthplaceDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
public class BirthplaceCreateDto {

    @Min(value = 1, message = "id cannot be less 1")
    private Long id;

    @Size(min = 2, message = "name must be more two symbol")
    private String birthplace;

    private String guid;

    @Min(value = 1, message = "author_id cannot be less 1")
    private Long author_id;

    private String status;
}
