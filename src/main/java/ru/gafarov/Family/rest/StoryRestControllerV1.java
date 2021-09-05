package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.StoryConverter;
import ru.gafarov.Family.dto.storyDto.StoryCreateDto;
import ru.gafarov.Family.dto.storyDto.StoryDto;
import ru.gafarov.Family.dto.storyDto.StoryShortDto;
import ru.gafarov.Family.exception_handling.*;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.Story;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.StoryService;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@RequestMapping("/api/v1/stories")
public class StoryRestControllerV1 {

    private final StoryService storyService;
    private final UserService userService;

    public StoryRestControllerV1(@Autowired StoryService storyService, @Autowired UserService userService) {
        this.storyService = storyService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDto> getStory(@PathVariable Long id){
        Story story = storyService.findById(id);
        if (story == null || !story.getStatus().equals(Status.ACTIVE)){
            throw new NotFoundException("story with id " + id + " not found");
        }
        StoryDto storyDto = StoryConverter.toStoryDto(story);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(storyDto, headers, HttpStatus.OK);
    }

    @GetMapping("/humans/{id}") // Получить истории по id человека
    public ResponseEntity<List<StoryShortDto>> getStoryByHumanId(@PathVariable Long id
            , @RequestHeader(value = "Authorization") String bearerToken){
        List<Story> storyList = storyService.getStoryByHumanId(id);
        List<StoryShortDto> storyDtoList = storyList.stream().filter(Objects::nonNull)
                .filter(a -> a.getStatus().equals(Status.ACTIVE))
                .map(StoryConverter::toStoryShortDto)
                .collect(Collectors.toList());
        if(storyDtoList.isEmpty()){
            throw new NotFoundException("There is no story with human by id " + id);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(storyDtoList, headers, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<StoryDto> addStory(@Valid @RequestBody StoryCreateDto storyCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(storyCreateDto.getId() != null || storyCreateDto.getAuthor_id() != null || storyCreateDto.getStatus() != null){
            throw new ConflictException("You cannot set id, author_id and status");
        }
        if(storyCreateDto.getStory() == null){
            throw new BadRequestException("story is required");
        }
        if(storyCreateDto.getHeroes_id() == null || storyCreateDto.getHeroes_id().length == 0){
            throw new BadRequestException("hero_id is required");
        }
        User me = userService.findMe(bearerToken);
        Story story = storyService.addStory(storyCreateDto, me);
        StoryDto storyDto = StoryConverter.toStoryDto(story);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(storyDto, headers, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<StoryDto> changeStory(@Valid @RequestBody StoryCreateDto storyCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(storyCreateDto.getId()==null){
            log.info("in changeStory(). id is required");
            throw new BadRequestException("id is required");
        }
        if(storyCreateDto.getStatus()!=null || storyCreateDto.getAuthor_id() != null){
            throw new ForbiddenException("you cannot change status or author_id");
        }
        if(storyCreateDto.getHeroes_id() != null && storyCreateDto.getHeroes_id().length == 0){
            throw new BadRequestException("hero_id is required");
        }
        User me = userService.findMe(bearerToken);
        Story story = storyService.changeStory(storyCreateDto, me);
        StoryDto storyDto = StoryConverter.toStoryDto(story);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(storyDto, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStory(@PathVariable Long id,  @RequestHeader(value = "Authorization") String bearerToken){

        User me = userService.findMe(bearerToken);
        storyService.deleteById(id, me);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new HashMap<>(){{put("message", "story with id " + id + " successfully deleted");}}, headers, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> exceptionHandler(ConflictException e){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> exceptionHandler(BadRequestException e){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> exceptionHandler(ForbiddenException e){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> exceptionHandler(NotFoundException e){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> handleException(ConstraintViolationException exception){
        MessageIncorrectData data = new MessageIncorrectData();
        data.setInfo(exception.getSQLException().getMessage());
        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(IllegalArgumentException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}