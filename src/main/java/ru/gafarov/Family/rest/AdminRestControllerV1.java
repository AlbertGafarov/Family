package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.*;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceCreateDto;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceMaxDto;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanMaxDto;
import ru.gafarov.Family.dto.photoDto.PhotoCreateDto;
import ru.gafarov.Family.dto.photoDto.PhotoMaxDto;
import ru.gafarov.Family.dto.storyDto.StoryCreateDto;
import ru.gafarov.Family.dto.storyDto.StoryMaxDto;
import ru.gafarov.Family.dto.surnameDto.SurnameCreateDto;
import ru.gafarov.Family.dto.surnameDto.SurnameMaxDto;
import ru.gafarov.Family.dto.userDto.UserCreateDto;
import ru.gafarov.Family.dto.userDto.UserMaxDto;
import ru.gafarov.Family.exception_handling.*;
import ru.gafarov.Family.model.*;
import ru.gafarov.Family.service.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/admin")
@RestController
public class AdminRestControllerV1 {

    private final UserService userService;
    private final HumanService humanService;
    private final BirthplaceService birthplaceService;
    private final SurnameService surnameService;
    private final PhotoService photoService;
    private final StoryService storyService;

    public AdminRestControllerV1(@Autowired UserService userService, @Autowired HumanService humanService, @Autowired BirthplaceService birthplaceService
            , @Autowired SurnameService surnameService, @Autowired PhotoService photoService, @Autowired StoryService storyService) {
        this.userService = userService;
        this.humanService = humanService;
        this.birthplaceService = birthplaceService;
        this.surnameService = surnameService;
        this.photoService = photoService;
        this.storyService = storyService;
    }

    @GetMapping("/users/{id}") // Получить полную информацию о пользователе
    public ResponseEntity<UserMaxDto> getUser(@PathVariable Long id){
        User user = userService.findById(id);
        return new ResponseEntity<>(UserConverter.toUserMaxDto(user), getJsonHeaders(), HttpStatus.OK);
    }

    @PutMapping("/users") // Изменить любые параметры пользователя.
    public ResponseEntity<UserMaxDto> updateUser(@Valid @RequestBody UserCreateDto userCreateDto
            , @RequestHeader Map<String, String> headers){
        log.info("IN updateUser(): Request: PUT /api/v1/admin/users \r\n\tHeaders = {} \r\n\tBody = {}", headers, userCreateDto);
        if(userCreateDto.getId()==null){
            throw new ConflictException("id is required");
        }
        User user = userService.changeUserInfo(null, userCreateDto);
        UserMaxDto userMaxDto = UserConverter.toUserMaxDto(user);
        System.out.println(userMaxDto);
        return new ResponseEntity<>(userMaxDto, getJsonHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}") // Удалить пользователя из бд
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id){
        log.info("IN deleteUser(): Request: DELETE /api/v1/admin/users/{}", id);
        userService.deleteById(id, null);
        return new ResponseEntity<>(new HashMap<>(){{put("message","User successfully deleted");}}, getJsonHeaders(), HttpStatus.OK);
    }

    @GetMapping("/humans/{id}") // Получить полную информацию о пользователе
    public ResponseEntity<HumanMaxDto> getHuman(@PathVariable Long id){
        Human human = humanService.findById(id);
        return new ResponseEntity<>(HumanConverter.toHumanMaxDto(human), getJsonHeaders(), HttpStatus.OK);
    }

    @PutMapping("/humans") // Изменить любые данные человека
    public ResponseEntity<HumanMaxDto> updateHuman(@Valid @RequestBody HumanCreateDto humanCreateDto){
        Human human = humanService.changeHumanInfo(humanCreateDto, null);
        return new ResponseEntity<>(HumanConverter.toHumanMaxDto(human), getJsonHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/humans/{id}") // Удалить человека из бд
    public ResponseEntity<Map<String, String>> deleteHuman(@PathVariable Long id){
        log.info("IN deleteHuman(): Request: DELETE /api/v1/admin/humans/{}", id);
        humanService.deleteById(id, null);
        return new ResponseEntity<>(new HashMap<>(){{put("message","Human successfully deleted");}}, getJsonHeaders(), HttpStatus.OK);
    }

    @GetMapping("/birthplaces/{id}") // Получить полную информацию о месте рождения
    public ResponseEntity<BirthplaceMaxDto> getBirthplace(@PathVariable Long id){
        Birthplace birthplace = birthplaceService.findById(id);
        return new ResponseEntity<>(BirthplaceConverter.toBirthplaceMaxDto(birthplace), getJsonHeaders(), HttpStatus.OK);
    }
    @PutMapping("/birthplaces") // Изменить любые данные места рождения
    public ResponseEntity<BirthplaceMaxDto> updateBirthplace(@Valid @RequestBody BirthplaceCreateDto birthplaceCreateDto){
        Birthplace birthplace = birthplaceService.changeBirthplace(birthplaceCreateDto, null);
        return new ResponseEntity<>(BirthplaceConverter.toBirthplaceMaxDto(birthplace), getJsonHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/birthplaces/{id}") // Удалить место рождения из бд
    public ResponseEntity<Map<String, String>> deleteBirthplace(@PathVariable Long id, @RequestHeader HttpHeaders headers){
        log.info("IN deleteBirthplace(): Request: DELETE /api/v1/admin/birthplaces/{} headers: {}", id, headers);
        birthplaceService.deleteById(id, null);
        return new ResponseEntity<>(new HashMap<>(){{put("message","Birthplace with id " + id + " successfully deleted from database");}}, getJsonHeaders(), HttpStatus.OK);
    }

    @GetMapping("/surnames/{id}") // Получить полную информацию о месте рождения
    public ResponseEntity<SurnameMaxDto> getSurname(@PathVariable Long id){
        Surname surname = surnameService.findById(id);
        return new ResponseEntity<>(SurnameConverter.toSurnameMaxDto(surname), getJsonHeaders(), HttpStatus.OK);
    }
    @PutMapping("/surnames") // Изменить любые данные места рождения
    public ResponseEntity<SurnameMaxDto> updateSurname(@Valid @RequestBody SurnameCreateDto surnameCreateDto){
        Surname surname = surnameService.changeSurname(surnameCreateDto, null);
        return new ResponseEntity<>(SurnameConverter.toSurnameMaxDto(surname), getJsonHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/surnames/{id}") // Удалить место рождения из бд
    public ResponseEntity<Map<String, String>> deleteSurname(@PathVariable Long id, @RequestHeader HttpHeaders headers){
        log.info("IN deleteSurname(): Request: DELETE /api/v1/admin/surnames/{} headers: {}", id, headers);
        surnameService.deleteById(id, null);
        return new ResponseEntity<>(new HashMap<>(){{put("message","Surname with id " + id + " successfully deleted from database");}}, getJsonHeaders(), HttpStatus.OK);
    }

    @GetMapping("/photos/{id}/info") // Получить полную информацию о фото
    public ResponseEntity<PhotoMaxDto> getPhoto(@PathVariable Long id) {
        Photo photo = photoService.findById(id);
        return new ResponseEntity<>(PhotoConverter.toPhotoMaxDto(photo), getJsonHeaders(), HttpStatus.OK);
    }

    @GetMapping("/photos/{id}") // Получить любой файл фото
    public void getPhotoFile(@PathVariable(name = "id") Long id, HttpServletResponse response
            , @RequestHeader(value = "Authorization") String bearerToken){
        Photo photo = photoService.findById(id);
        File photoFile = photoService.getPhotoFile(id);
        Path path = photoFile.toPath();
        if(Files.exists(path)){
            response.setHeader("Content-Disposition", "attachment;filename=" + photo.getName());
            response.setContentType("image/jpeg");
        } else {
            throw new NotFoundException("Not found file " + path);
        }
        try {
            Files.copy(path, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @PutMapping("/photos") // Изменить любые данные фото
    public ResponseEntity<PhotoMaxDto> updatePhoto(@Valid @RequestBody PhotoCreateDto photoCreateDto){
        Photo photo = photoService.changePhoto(photoCreateDto, null);
        return new ResponseEntity<>(PhotoConverter.toPhotoMaxDto(photo), getJsonHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/photos/{id}") // Удалить фото из бд и файл из хранилища
    public ResponseEntity<Map<String, String>> deletePhoto(@PathVariable Long id, @RequestHeader HttpHeaders headers){
        log.info("IN deletePhoto(): Request: DELETE /api/v1/admin/photos/{} headers: {}", id, headers);
        photoService.deleteById(id, null);
        return new ResponseEntity<>(new HashMap<>(){{put("message","Photo with id " + id + " successfully deleted from database");}}, getJsonHeaders(), HttpStatus.OK);
    }

    @GetMapping("/stories/{id}") // Получить полную информацию о истории
    public ResponseEntity<StoryMaxDto> getStory(@PathVariable Long id){
        Story story = storyService.findById(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(StoryConverter.toStoryMaxDto(story), responseHeaders, HttpStatus.OK);
    }
    @PutMapping("/stories") // Изменить любые данные истории
    public ResponseEntity<StoryMaxDto> updateStory(@Valid @RequestBody StoryCreateDto storyCreateDto){
        Story story = storyService.changeStory(storyCreateDto, null);
        return new ResponseEntity<>(StoryConverter.toStoryMaxDto(story), getJsonHeaders(), HttpStatus.OK);
    }

    @DeleteMapping("/stories/{id}") // Удалить историю из бд
    public ResponseEntity<Map<String, String>> deleteStory(@PathVariable Long id, @RequestHeader HttpHeaders headers){
        log.info("IN deleteStory(): Request: DELETE /api/v1/admin/stories/{} headers: {}", id, headers);
        storyService.deleteById(id, null);
        return new ResponseEntity<>(new HashMap<>(){{put("message","Story with id " + id + " successfully deleted from database");}}, getJsonHeaders(), HttpStatus.OK);
    }

    private HttpHeaders getJsonHeaders(){
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        responseHeaders.add("Charset", "UTF-8");
        return responseHeaders;
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
    public ResponseEntity<MessageIncorrectData> exceptionHandler(ConflictException e){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(e.getMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(EmptyResultDataAccessException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(RegisterException exception){
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
