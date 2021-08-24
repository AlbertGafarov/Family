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
import ru.gafarov.Family.converter.BirthplaceConverter;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceCreateDto;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceDto;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceShortDto;
import ru.gafarov.Family.exception_handling.*;
import ru.gafarov.Family.model.Birthplace;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.BirthplaceService;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@RequestMapping("/api/v1/birthplaces")
public class BirthPlaceRestControllerV1 {

    private final BirthplaceService birthplaceService;
    private final UserService userService;

    public BirthPlaceRestControllerV1(@Autowired BirthplaceService birthplaceService, @Autowired UserService userService) {
        this.birthplaceService = birthplaceService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BirthplaceDto> getBirthplace(@PathVariable Long id){
        Birthplace birthplace = birthplaceService.findById(id);
        if (birthplace == null || !birthplace.getStatus().equals(Status.ACTIVE)){
            throw new NotFoundException("birthplace with id " + id + " not found");
        }
        BirthplaceDto birthplaceDto = BirthplaceConverter.toBirthplaceDto(birthplace);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(birthplaceDto, headers, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<BirthplaceDto> addBirthPlace(@Valid @RequestBody BirthplaceCreateDto birthplaceCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(birthplaceCreateDto.getId()!=null || birthplaceCreateDto.getAuthor_id() != null || birthplaceCreateDto.getStatus() != null){
            throw new ConflictException("You cannot set id, author_id and status");
        }
        if(birthplaceCreateDto.getBirthplace()==null){
            throw new BadRequestException("birthplace is required");
        }
        User me = userService.findMe(bearerToken);
        Birthplace birthplace = birthplaceService.addBirthplace(birthplaceCreateDto, me);
        BirthplaceDto birthplaceDto = BirthplaceConverter.toBirthplaceDto(birthplace);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(birthplaceDto, headers, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<BirthplaceDto> changeBirthPlace(@Valid @RequestBody BirthplaceCreateDto birthplaceCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(birthplaceCreateDto.getId()==null){
            log.info("in changeBirthPlace(). id is required");
            throw new BadRequestException("id is required");
        }
        if(birthplaceCreateDto.getStatus()!=null || birthplaceCreateDto.getAuthor_id() != null){
            throw new ForbiddenException("you cannot change status or author_id");
        }
        User me = userService.findMe(bearerToken);
        Birthplace birthplace = birthplaceService.changeBirthplace(birthplaceCreateDto, me);
        BirthplaceDto birthplaceDto = BirthplaceConverter.toBirthplaceDto(birthplace);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(birthplaceDto, headers, HttpStatus.OK);
    }

    @GetMapping("/search_birthplaces/{partOfName}") // Поиск места рождения по части названия.
    public ResponseEntity<List<BirthplaceShortDto>> searchHumans(@PathVariable String partOfName, @RequestHeader(value = "Authorization") String bearerToken) {
        if (partOfName.length() < 3) {
            throw new ConflictException("You entered part of name too short. Please enter more than 2 symbols");
        }
        if (partOfName.length() > 30) {
            throw new ConflictException("Yoy entered part of too long. Please enter less than 15 symbols");
        }
        List<Birthplace> listOfBirthplaces = birthplaceService.searchBirthplaces(partOfName);

        List<BirthplaceShortDto> birthplaceShortDtos = listOfBirthplaces.stream()
                .filter(a -> a.getStatus().equals(Status.ACTIVE))
                .map(BirthplaceConverter::toBirthplaceShortDto)
                .sorted(Comparator.comparingLong(BirthplaceShortDto::getId))
                .collect(Collectors.toList());

        if (birthplaceShortDtos.isEmpty()) {
            throw new NotFoundException("There is no birthplaces like: " + partOfName);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(birthplaceShortDtos, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBirthplace(@PathVariable Long id,  @RequestHeader(value = "Authorization") String bearerToken){

        User me = userService.findMe(bearerToken);
        birthplaceService.deleteById(id, me);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new HashMap<>(){{put("message", "birthplace with id " + id + " successfully deleted");}}, headers, HttpStatus.OK);
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
