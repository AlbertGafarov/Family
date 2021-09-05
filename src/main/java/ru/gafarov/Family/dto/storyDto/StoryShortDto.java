package ru.gafarov.Family.dto.storyDto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class StoryShortDto {
    private Long id;
    private String story;
}