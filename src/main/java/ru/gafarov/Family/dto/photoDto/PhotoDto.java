package ru.gafarov.Family.dto.photoDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties
public class PhotoDto {
    private Long id;
    private String name;
}
