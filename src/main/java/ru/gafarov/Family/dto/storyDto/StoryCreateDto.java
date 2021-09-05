package ru.gafarov.Family.dto.storyDto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
public class StoryCreateDto {

    @Min(value = 1, message = "id cannot be less 1")
    private Long id;

    @Size(min = 10, message = "name must be more ten symbols")
    private String story;

    @Min(value = 1, message = "author_id cannot be less 1")
    private Long author_id;

    private Long[] heroes_id;

    private String status;
}
