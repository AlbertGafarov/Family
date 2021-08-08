package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.dto.userDto.UserFullDto;
import ru.gafarov.Family.dto.userDto.UserRegisterDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.exception_handling.NoSuchUserException;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.UserService;

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

    @PutMapping("/users")
    public ResponseEntity<UserFullDto> updateUser(@RequestBody UserRegisterDto userRegisterDto
            , @RequestHeader Map<String, String> headers){
        log.info("IN updateUser(): Request: DELETE /api/v1/admin/users \r\n\tHeaders = {} \r\n\tBody = {}", headers, userRegisterDto);
        if(userRegisterDto.getPassword()!=null){
            throw new NoSuchUserException("You can't change password here. Try again without password");
        }
        UserFullDto userFullDto = userService.changeUserInfo(userRegisterDto);
        return new ResponseEntity<>(userFullDto, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(EmptyResultDataAccessException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
