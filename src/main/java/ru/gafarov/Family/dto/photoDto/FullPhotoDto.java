package ru.gafarov.Family.dto.photoDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;

import java.util.List;

@JsonIgnoreProperties
@Data
public class FullPhotoDto {
    private Long id;
    private String name;
    private String photo_date;
    private List<HumanShortDto> humans_on_photo;
}
