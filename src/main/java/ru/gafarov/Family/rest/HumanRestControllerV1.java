package ru.gafarov.Family.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/humans")
public class HumanRestControllerV1 {

    private final HumanService humanService;
    private final UserService userService;

    public HumanRestControllerV1(@Autowired HumanService humanService, @Autowired UserService userService) {
        this.humanService = humanService;
        this.userService = userService;
    }

    @GetMapping("/{id}") //Получить информацию о человеке
    public ResponseEntity<HumanDto> getHumanInfo(@PathVariable Long id){
        Human human = humanService.getHuman(id);
        HumanDto humanDto = HumanConverter.toHumanDto(human);
        return new ResponseEntity<>(humanDto, HttpStatus.OK);
    }

    @GetMapping("") //Получить информацию обо всех людях
    public ResponseEntity<List<HumanDto>> getAllHumans(@RequestHeader(value = "Authorization") String bearerToken){
        List<HumanDto> humanDtoList = humanService.getAllHumansDto();
        return new ResponseEntity<>(humanDtoList, HttpStatus.OK);
    }

    @GetMapping("/full_info/{id}") //Получить подробную информацию о человеке
    public ResponseEntity<HumanFullDto> getHumanFullInfo(@PathVariable Long id, @RequestHeader(value = "Authorization") String bearerToken){
        Human human = humanService.getHuman(id);
        HumanFullDto humanFullDto = HumanConverter.toHumanFullDto(human);
        return new ResponseEntity<>(humanFullDto, HttpStatus.OK);
    }

    @PostMapping("") //Добавить человека
    public ResponseEntity<HumanFullDto> addHuman(@Valid @RequestBody HumanCreateDto humanCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(humanCreateDto.getAuthor_id() != null){
            throw new NoSuchHumanException("You cannot set author for human. Please ask Admin for this.");
        }
        User me = userService.findMe(bearerToken);
        HumanFullDto humanFullDto = humanService.addHuman(humanCreateDto, me);

        return new ResponseEntity<>(humanFullDto, HttpStatus.OK);
    }

    @GetMapping("/search_human/{partOfName}") // Поиск человека по части имени, фамилии.
    public ResponseEntity<List<HumanDto>> searchHumans(@PathVariable String partOfName, @RequestHeader(value = "Authorization") String bearerToken) {
        if (partOfName.length() < 3) {
            throw new NoSuchHumanException("You entered part of name too short. Please enter more than 2 symbols");
        }
        if (partOfName.length() > 15) {
            throw new NoSuchHumanException("Yoy entered part of too long. Please enter less than 15 symbols");
        }
        List<Human> listOfHuman = humanService.searchHuman(partOfName);

        if (listOfHuman.isEmpty()) {
            throw new NoSuchHumanException("There is no people like: " + partOfName);
        }

        List<HumanDto> listOfHumanDto = listOfHuman.stream()
                .map(HumanConverter::toHumanDto)
                .sorted(Comparator.comparing(HumanDto::getId))
                .collect(Collectors.toList());

        return new ResponseEntity<>(listOfHumanDto, HttpStatus.OK);
    }
    @PutMapping("/change_info")// Изменить или добавить информацию о человеке
    public ResponseEntity<HumanFullDto> update(@Valid @RequestBody HumanCreateDto humanCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        User me = userService.findMe(bearerToken);
        if(humanCreateDto.getAuthor_id() != null){
            throw new NoSuchHumanException("You cannot change author for human. Please ask Admin for this.");
        }
        Human newHuman = humanService.changeHumanInfo(humanCreateDto, me);
        HumanFullDto humanFullDto = HumanConverter.toHumanFullDto(newHuman);
        return new ResponseEntity<>(humanFullDto, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NoSuchHumanException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
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
