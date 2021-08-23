package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.BirthplaceConverter;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.converter.UserConverter;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceCreateDto;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceMaxDto;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanMaxDto;
import ru.gafarov.Family.dto.userDto.UserCreateDto;
import ru.gafarov.Family.dto.userDto.UserMaxDto;
import ru.gafarov.Family.exception_handling.*;
import ru.gafarov.Family.model.Birthplace;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.BirthplaceService;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/admin")
@RestController
public class AdminRestControllerV1 {

    private final UserService userService;
    private final HumanService humanService;
    private final BirthplaceService birthplaceService;

    public AdminRestControllerV1(@Autowired UserService userService, @Autowired HumanService humanService, @Autowired BirthplaceService birthplaceService) {
        this.userService = userService;
        this.humanService = humanService;
        this.birthplaceService = birthplaceService;
    }

    @GetMapping("/users/{id}") // Получить полную информацию о пользователе
    public ResponseEntity<UserMaxDto> getUser(@PathVariable Long id){
        User user = userService.findById(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(UserConverter.toUserMaxDto(user), responseHeaders, HttpStatus.OK);
    }

    @PutMapping("/users") // Изменить любые параметры пользователя.
    public ResponseEntity<UserMaxDto> updateUser(@Valid @RequestBody UserCreateDto userCreateDto
            , @RequestHeader Map<String, String> headers){
        log.info("IN updateUser(): Request: PUT /api/v1/admin/users \r\n\tHeaders = {} \r\n\tBody = {}", headers, userCreateDto);
        if(userCreateDto.getId()==null){
            throw new NoSuchUserException("id is required");
        }
        User user = userService.changeUserInfo(null, userCreateDto);
        UserMaxDto userMaxDto = UserConverter.toUserMaxDto(user);
        System.out.println(userMaxDto);
        return new ResponseEntity<>(userMaxDto, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}") // Удалить пользователя из бд
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id){
        log.info("IN deleteUser(): Request: DELETE /api/v1/admin/users/{}", id);
        userService.deleteById(id, null);
        return new ResponseEntity<>(new HashMap<>(){{put("message","User successfully deleted");}}, HttpStatus.OK);
    }

    @GetMapping("/humans/{id}") // Получить полную информацию о пользователе
    public ResponseEntity<HumanMaxDto> getHuman(@PathVariable Long id){
        Human human = humanService.getHuman(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(HumanConverter.toHumanMaxDto(human), responseHeaders, HttpStatus.OK);
    }

    @PutMapping("/humans") // Изменить любые данные человека
    public ResponseEntity<HumanMaxDto> updateHuman(@Valid @RequestBody HumanCreateDto humanCreateDto){
        Human human = humanService.changeHumanInfo(humanCreateDto, null);
        return new ResponseEntity<>(HumanConverter.toHumanMaxDto(human), HttpStatus.OK);
    }

    @DeleteMapping("/humans/{id}") // Удалить человека из бд
    public ResponseEntity<Map<String, String>> deleteHuman(@PathVariable Long id){
        log.info("IN deleteHuman(): Request: DELETE /api/v1/admin/humans/{}", id);
        humanService.deleteById(id);
        return new ResponseEntity<>(new HashMap<>(){{put("message","Human successfully deleted");}}, HttpStatus.OK);
    }

    @GetMapping("/birthplaces/{id}") // Получить полную информацию о месте рождения
    public ResponseEntity<BirthplaceMaxDto> getBirthPlace(@PathVariable Long id){
        Birthplace birthplace = birthplaceService.findById(id);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(BirthplaceConverter.toBirthplaceMaxDto(birthplace), responseHeaders, HttpStatus.OK);
    }
    @PutMapping("/birthplaces") // Изменить любые данные места рождения
    public ResponseEntity<BirthplaceMaxDto> updateBirthplace(@Valid @RequestBody BirthplaceCreateDto birthplaceCreateDto){
        Birthplace birthplace = birthplaceService.changeBirthplace(birthplaceCreateDto, null);
        return new ResponseEntity<>(BirthplaceConverter.toBirthplaceMaxDto(birthplace), HttpStatus.OK);
    }

    @DeleteMapping("/birthplaces/{id}") // Удалить место рождения из бд
    public ResponseEntity<Map<String, String>> deleteBirthplace(@PathVariable Long id, @RequestHeader HttpHeaders headers){
        log.info("IN deleteBirthplace(): Request: DELETE /api/v1/admin/birthplaces/{} headers: {}", id, headers);
        birthplaceService.deleteById(id, null);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        return new ResponseEntity<>(new HashMap<>(){{put("message","Birthplace with id " + id + " successfully deleted from database");}}, responseHeaders, HttpStatus.OK);
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
    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NoSuchUserException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NoSuchHumanException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
