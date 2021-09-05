package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.converter.StoryConverter;
import ru.gafarov.Family.dto.storyDto.StoryCreateDto;
import ru.gafarov.Family.exception_handling.ConflictException;
import ru.gafarov.Family.exception_handling.ForbiddenException;
import ru.gafarov.Family.exception_handling.NotFoundException;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.Story;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.repository.StoryRepository;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.StoryService;
import ru.gafarov.Family.service.UserService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StoryServiceImpl implements StoryService {

    private final StoryRepository storyRepository;
    private final UserService userService;
    private final HumanService humanService;

    public StoryServiceImpl(@Autowired StoryRepository storyRepository, @Autowired UserService userService, @Autowired HumanService humanService) {
        this.storyRepository = storyRepository;
        this.userService = userService;
        this.humanService = humanService;
    }

    @Override
    public Story findById(Long id) {
        Story story = storyRepository.findById(id).orElse(null);
        if(story == null){
            throw new NotFoundException("Not found story with id: " + id);
        }
        return story;
    }

    @Override
    public List<Story> getStoryByHumanId(Long id) { //получить список фото для человека по id
        List<Story> storyList = storyRepository.getStoryByHumanId(id);
        log.info("storyList = {}", storyList.stream().map(StoryConverter::toStoryDto));
        return storyList;
    }

    @Override
    public Story addStory(StoryCreateDto storyCreateDto, User me) {
        Story story = Story.builder()
                .story(storyCreateDto.getStory())
                .author(me)
                .heroes(Arrays.stream(storyCreateDto.getHeroes_id()).map(humanService::findById).collect(Collectors.toList()))
                .created(new Date())
                .updated(new Date())
                .status(Status.ACTIVE)
                .build();

        return storyRepository.save(story);
    }

    @Override
    public Story changeStory(StoryCreateDto storyCreateDto, User me) {
        Story story = storyRepository.findById(storyCreateDto.getId()).orElse(null);
        if (story == null){
            throw new NotFoundException("story with id " + storyCreateDto.getId() + " not found");
        }
        if (!story.getStatus().equals(Status.ACTIVE) && me != null){
            log.info("in changeStory(). Not found exception, because status = {}", story.getStatus());
            throw new NotFoundException("Not found story with id: " + story.getId());
        }
        if(!story.getAuthor().equals(me) && me!=null){
            throw new ForbiddenException("You are not author story with id = " + storyCreateDto.getId() + ", so you cannot change this");
        }
        if(storyCreateDto.getStory() != null){
            story.setStory(storyCreateDto.getStory());
        }
        if(storyCreateDto.getHeroes_id() != null){
            story.setHeroes(Arrays.stream(storyCreateDto.getHeroes_id()).map(humanService::findById).collect(Collectors.toList()));
        }
        if(storyCreateDto.getAuthor_id()!=null && me == null){ //Изменить автора может только админ, админ передает: me = null
            User author = userService.findById(storyCreateDto.getAuthor_id());
            if (author == null){
                log.info("author not found with id = {}", storyCreateDto.getAuthor_id());
                throw new NotFoundException("author with id " + storyCreateDto.getAuthor_id() + "not found");
            }
            story.setAuthor(author);
        }
        if(storyCreateDto.getStatus()!=null && me == null){ // Изменить статус может только админ, админ передает: me = null
            story.setStatus(Status.valueOf(storyCreateDto.getStatus()));
        }
        story.setUpdated(new Date());
        return storyRepository.save(story);
    }

    @Override
    public void deleteById(Long id, User me) {
        Story story = findById(id);
        if(me != null){
            if (!story.getStatus().equals(Status.ACTIVE)){
                log.info("in deleteById(). Not found exception, because status = {}", story.getStatus());
                throw new NotFoundException("Not found story with id: " + id);
            }
            else if (!story.getAuthor().equals(me)){
                log.info("in deleteById(). You are not author story with id = {}, so you cannot delete this", story.getId());
                throw new ConflictException("You are not author story with id:" + story.getId() + ", so you cannot delete this");
            }
            story.setStatus(Status.DELETED);
            storyRepository.save(story); // Если удаляет пользователь, то запись помечается удаленной
        } else {
            storyRepository.deleteById(id); // Если удаляет админ, то запись удаляется из бд.
        }
    }
}