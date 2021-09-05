package ru.gafarov.Family.dto.storyDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class StoryDto extends StoryShortDto {
    private List<HumanShortDto> heroes;
}
