package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.exception_handling.*;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.UserService;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
        Human human = humanService.findById(id);
        if(!human.getStatus().equals(Status.ACTIVE)){
            log.info("human with id {} not found, because status = {}", id, human.getStatus());
            throw new NotFoundException("human with id " + id + " not found");
        }
        HumanDto humanDto = HumanConverter.toHumanDto(human);
        return new ResponseEntity<>(humanDto, HttpStatus.OK);
    }

    @GetMapping("") //Получить информацию обо всех людях
    public ResponseEntity<List<HumanDto>> getAllHumans(@RequestHeader(value = "Authorization") String bearerToken){
        List<Human> humanList = humanService.getAllHumans();
        List<HumanDto> humanDtoList = humanList.stream()
                .filter(a -> a.getStatus().equals(Status.ACTIVE))
                .map(HumanConverter::toHumanDto)
                .collect(Collectors.toList());
        if(humanDtoList.isEmpty()){
            throw new NotFoundException("Humans not found. Please ask admin");
        }
        return new ResponseEntity<>(humanDtoList, HttpStatus.OK);
    }

    @GetMapping("/full_info/{id}") //Получить подробную информацию о человеке
    public ResponseEntity<HumanFullDto> getHumanFullInfo(@PathVariable Long id, @RequestHeader(value = "Authorization") String bearerToken){
        Human human = humanService.findById(id);
        if(!human.getStatus().equals(Status.ACTIVE)){
            log.info("human with id {} not found, because status = {}", id, human.getStatus());
            throw new NotFoundException("human with id " + id + " not found");
        }
        HumanFullDto humanFullDto = HumanConverter.toHumanFullDto(human);
        return new ResponseEntity<>(humanFullDto, HttpStatus.OK);
    }

    @GetMapping("/parents/{id}")
    public ResponseEntity<List<HumanDto>> getParents(@PathVariable Long id, @RequestHeader(value = "Authorization") String bearerToken){
        Human human = humanService.findById(id);
        if(!human.getStatus().equals(Status.ACTIVE)){
            log.info("human with id {} not found, because status = {}", id, human.getStatus());
            throw new NotFoundException("human with id " + id + " not found");
        }
        List<HumanDto> parentsDto = human.getParents().stream().filter(a -> a.getStatus().equals(Status.ACTIVE)).map(HumanConverter::toHumanDto).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(parentsDto, headers, HttpStatus.OK);
    }

    @GetMapping("/children/{id}")
    public ResponseEntity<List<HumanDto>> getChildren(@PathVariable Long id, @RequestHeader(value = "Authorization") String bearerToken){
        Human human = humanService.findById(id);
        if(!human.getStatus().equals(Status.ACTIVE)){
            log.info("human with id {} not found, because status = {}", id, human.getStatus());
            throw new NotFoundException("human with id " + id + " not found");
        }
        List<HumanDto> chidrenDto = human.getChildren().stream().filter(a -> a.getStatus().equals(Status.ACTIVE)).map(HumanConverter::toHumanDto).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(chidrenDto, headers, HttpStatus.OK);
    }

    @GetMapping("/brother-and-sisters/{id}/{n}/{gender}") // n - степень родства. Например 2 - это "двоюрдные"
    public ResponseEntity<List<HumanDto>> getBrothersAndSisters(@PathVariable(name = "id") Long id
            , @PathVariable(name = "n") int n
            , @PathVariable(name = "gender") String gender
            , @RequestHeader(value = "Authorization") String bearerToken){
        final String gen = gender.toUpperCase(Locale.ROOT);
        Human human = humanService.findById(id);
        if(!human.getStatus().equals(Status.ACTIVE)){
            log.info("human with id {} not found, because status = {}", id, human.getStatus());
            throw new NotFoundException("human with id " + id + " not found");
        }
        List<HumanDto> brothersAndSistersDto = humanService.getBrothersAndSisters(human, n).stream()
                .filter(a -> gen.equals("ALL") || a.getGender().equals(gen))
                .map(HumanConverter::toHumanDto).collect(Collectors.toList());
        if(brothersAndSistersDto.isEmpty()){
            if(gen.equals("M")) {
                throw new NotFoundException("Not found brothers");
            } else if(gen.equals("W")){
                throw new NotFoundException("Not found sisters");
            } else {
                throw new NotFoundException("Not found brothers and sisters");
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(brothersAndSistersDto, headers, HttpStatus.OK);
    }

    @PostMapping("") //Добавить человека
    public ResponseEntity<HumanFullDto> addHuman(@Valid @RequestBody HumanCreateDto humanCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        if(humanCreateDto.getAuthor_id() != null || humanCreateDto.getStatus() != null || humanCreateDto.getId() != null){
            throw new BadRequestException("You cannot set id, author or status for human.");
        }
        User me = userService.findMe(bearerToken);
        Human human = humanService.addHuman(humanCreateDto, me);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(HumanConverter.toHumanFullDto(human), headers, HttpStatus.OK);
    }

    @GetMapping("/search_human/{partOfName}") // Поиск человека по части имени, фамилии.
    public ResponseEntity<List<HumanDto>> searchHumans(@PathVariable String partOfName, @RequestHeader(value = "Authorization") String bearerToken) {
        if (partOfName.length() < 3) {
            throw new BadRequestException("You entered part of name too short. Please enter more than 2 symbols");
        }
        if (partOfName.length() > 15) {
            throw new BadRequestException("Yoy entered part of too long. Please enter less than 15 symbols");
        }
        List<Human> listOfHuman = humanService.searchHuman(partOfName);

        if (listOfHuman.isEmpty()) {
            throw new NotFoundException("There is no people like: " + partOfName);
        }
        List<HumanDto> listOfHumanDto = listOfHuman.stream()
                .filter(a -> a.getStatus().equals(Status.ACTIVE))
                .map(HumanConverter::toHumanDto)
                .sorted(Comparator.comparing(HumanDto::getId))
                .collect(Collectors.toList());
        return new ResponseEntity<>(listOfHumanDto, HttpStatus.OK);
    }
    @PutMapping("/change_info")// Изменить или добавить информацию о человеке
    public ResponseEntity<HumanFullDto> update(@Valid @RequestBody HumanCreateDto humanCreateDto, @RequestHeader(value = "Authorization") String bearerToken){
        User me = userService.findMe(bearerToken);
        if(humanCreateDto.getAuthor_id() != null){
            throw new ConflictException("You cannot change author for human. Please ask Admin for this.");
        }
        Human newHuman = humanService.changeHumanInfo(humanCreateDto, me);
        HumanFullDto humanFullDto = HumanConverter.toHumanFullDto(newHuman);
        return new ResponseEntity<>(humanFullDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteHuman(@PathVariable Long id, @RequestHeader(value = "Authorization") String bearerToken){

        User me = userService.findMe(bearerToken);
        humanService.deleteById(id, me);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new HashMap<>(){{put("message", "human with id " + id + " successfully deleted");}}, headers, HttpStatus.OK);
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

}
