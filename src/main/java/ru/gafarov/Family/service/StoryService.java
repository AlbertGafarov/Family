package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.storyDto.StoryCreateDto;
import ru.gafarov.Family.model.Story;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface StoryService {

    Story findById(Long id);
    List<Story> getStoryByHumanId(Long id);
    Story addStory(StoryCreateDto storyCreateDto, User me);
    Story changeStory(StoryCreateDto storyCreateDto, User me);
    void deleteById(Long id, User me);
}