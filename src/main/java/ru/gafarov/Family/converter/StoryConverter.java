package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.storyDto.StoryDto;
import ru.gafarov.Family.dto.storyDto.StoryMaxDto;
import ru.gafarov.Family.dto.storyDto.StoryShortDto;
import ru.gafarov.Family.model.Story;

import java.util.stream.Collectors;

public class StoryConverter {

    public static StoryShortDto toStoryShortDto(Story story) {
        return StoryShortDto.builder()
                .id(story.getId())
                .story(story.getStory())
                .build();
    }

    public static StoryDto toStoryDto(Story story) {
        return StoryDto.builder()
                .id(story.getId())
                .story(story.getStory())
                .heroes(story.getHeroes().stream().map(HumanConverter::toHumanShortDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static StoryMaxDto toStoryMaxDto(Story story) {
        return StoryMaxDto.builder()
                .id(story.getId())
                .story(story.getStory())
                .heroes(story.getHeroes().stream().map(HumanConverter::toHumanShortDto)
                        .collect(Collectors.toList()))
                .author(UserConverter.toUserDto(story.getAuthor()))
                .status(story.getStatus())
                .updated(story.getUpdated())
                .created(story.getCreated())
                .build();
    }
}
