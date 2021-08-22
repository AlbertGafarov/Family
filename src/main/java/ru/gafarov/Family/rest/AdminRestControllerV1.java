package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.converter.UserConverter;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanMaxDto;
import ru.gafarov.Family.dto.userDto.UserCreateDto;
import ru.gafarov.Family.dto.userDto.UserMaxDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.exception_handling.NoSuchUserException;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/admin")
@RestController
public class AdminRestControllerV1 {

    @Autowired
    UserService userService;

    @Autowired
    HumanService humanService;

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id){
        log.info("IN deleteUser(): Request: DELETE /api/v1/admin/users/{}", id);
        userService.deleteById(id);
        return new ResponseEntity<>(new HashMap<>(){{put("message","User successfully deleted");}}, HttpStatus.OK);
    }

    @DeleteMapping("/humans/{id}")
    public ResponseEntity<Map<String, String>> deleteHuman(@PathVariable Long id){
        log.info("IN deleteHuman(): Request: DELETE /api/v1/admin/humans/{}", id);
        humanService.deleteById(id);
        return new ResponseEntity<>(new HashMap<>(){{put("message","Human successfully deleted");}}, HttpStatus.OK);
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

    @PutMapping("/humans")
    public ResponseEntity<HumanMaxDto> updateHuman(@Valid @RequestBody HumanCreateDto humanCreateDto){
        Human human = humanService.changeHumanInfo(humanCreateDto, null);
        return new ResponseEntity<>(HumanConverter.toHumanMaxDto(human), HttpStatus.OK);
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
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NoSuchHumanException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }
}
