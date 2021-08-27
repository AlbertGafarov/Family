package ru.gafarov.Family.dto.photoDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties
@Data
@SuperBuilder
public class PhotoFullDto extends PhotoDto {
    private Long size;
    private Integer height;
    private Integer width;
    private List<HumanShortDto> humansOnPhoto;
}
