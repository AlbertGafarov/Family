package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.dto.userDto.UserCreateDto;
import ru.gafarov.Family.dto.userDto.UserDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class RegisterRestControllerV1 {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register( @Valid @RequestBody UserCreateDto userCreateDto){
        UserDto userDto = userService.register(userCreateDto);
        log.info("POST api/v1/register body: {}", userCreateDto);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> handleException(ConstraintViolationException exception){
        MessageIncorrectData data = new MessageIncorrectData();
        data.setInfo(exception.getSQLException().getMessage());
        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> handleException(RegisterException exception){
        MessageIncorrectData data = new MessageIncorrectData();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.CONFLICT);
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
