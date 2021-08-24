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
import ru.gafarov.Family.converter.SurnameConverter;
import ru.gafarov.Family.dto.surnameDto.SurnameCreateDto;
import ru.gafarov.Family.dto.surnameDto.SurnameDto;
import ru.gafarov.Family.exception_handling.*;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.Surname;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.SurnameService;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@RequestMapping("/api/v1/surnames")
public class SurnameRestControllerV1 {

    private final SurnameService surnameService;
    private final UserService userService;

    public SurnameRestControllerV1(@Autowired SurnameService surnameService, @Autowired UserService userService) {
        this.surnameService = surnameService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurnameDto> getSurname(@PathVariable Long id){
        Surname surname = surnameService.findById(id);
        if (surname == null || !surname.getStatus().equals(Status.ACTIVE)){
            throw new NotFoundException("surname with id " + id + " not found");
        }
        SurnameDto surnameDto = SurnameConverter.toSurnameDto(surname);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(surnameDto, headers, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<SurnameDto> addSurname(@Valid @RequestBody SurnameCreateDto surnameCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(surnameCreateDto.getId()!=null || surnameCreateDto.getAuthor_id() != null || surnameCreateDto.getStatus() != null){
            throw new ConflictException("You cannot set id, author_id and status");
        }
        if(surnameCreateDto.getSurname()==null){
            throw new BadRequestException("surname is required");
        }
        User me = userService.findMe(bearerToken);
        Surname surname = surnameService.addSurname(surnameCreateDto, me);
        SurnameDto surnameDto = SurnameConverter.toSurnameDto(surname);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(surnameDto, headers, HttpStatus.OK);
    }
    @PutMapping("")
    public ResponseEntity<SurnameDto> changeSurname(@Valid @RequestBody SurnameCreateDto surnameCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(surnameCreateDto.getId()==null){
            log.info("in changeSurname(). id is required");
            throw new BadRequestException("id is required");
        }
        if(surnameCreateDto.getStatus()!=null || surnameCreateDto.getAuthor_id() != null){
            throw new ForbiddenException("you cannot change status or author_id");
        }
        User me = userService.findMe(bearerToken);
        Surname surname = surnameService.changeSurname(surnameCreateDto, me);
        SurnameDto surnameDto = SurnameConverter.toSurnameDto(surname);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(surnameDto, headers, HttpStatus.OK);
    }

    @GetMapping("/search_surnames/{partOfSurname}") // Поиск места рождения по части названия.
    public ResponseEntity<List<SurnameDto>> searchHumans(@PathVariable String partOfSurname, @RequestHeader(value = "Authorization") String bearerToken) {
        if (partOfSurname.length() < 3) {
            throw new ConflictException("You entered part of name too short. Please enter more than 2 symbols");
        }
        if (partOfSurname.length() > 30) {
            throw new ConflictException("Yoy entered part of too long. Please enter less than 15 symbols");
        }
        List<Surname> listOfSurnames = surnameService.searchSurnames(partOfSurname);

        List<SurnameDto> surnameDtos = listOfSurnames.stream()
                .filter(a -> a.getStatus().equals(Status.ACTIVE))
                .map(SurnameConverter::toSurnameDto)
                .sorted(Comparator.comparingLong(SurnameDto::getId))
                .collect(Collectors.toList());

        if (surnameDtos.isEmpty()) {
            throw new NotFoundException("There is no surnames like: " + partOfSurname);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(surnameDtos, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteSurname(@PathVariable Long id, @RequestHeader(value = "Authorization") String bearerToken){

        User me = userService.findMe(bearerToken);
        surnameService.deleteById(id, me);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new HashMap<>(){{put("message", "surname with id " + id + " successfully deleted");}}, headers, HttpStatus.OK);
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
