package ru.gafarov.Family.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.service.HumanService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/humans")
public class HumanRestControllerV1 {

    @Autowired
    HumanService humanService;

    @GetMapping("/{id}")
    public ResponseEntity<HumanDto> getHumanInfo(@PathVariable Long id){
        HumanDto humanDto = humanService.getHumanDto(id);
        return new ResponseEntity<>(humanDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<HumanDto>> getAllHumans(){
        List<HumanDto> humanDtoList = humanService.getAllHumansDto();
        return new ResponseEntity<>(humanDtoList, HttpStatus.OK);
    }

    @GetMapping("/full_info/{id}")
    public ResponseEntity<HumanFullDto> getHumanFullInfo(@PathVariable Long id){
        HumanFullDto humanFullDtoDto = humanService.getHumanFullDto(id);
        return new ResponseEntity<>(humanFullDtoDto, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<HumanFullDto> addHuman(@RequestBody HumanCreateDto humanCreateDto){
        HumanFullDto humanFullDto = humanService.addHuman(humanCreateDto);

        return new ResponseEntity<>(humanFullDto, HttpStatus.OK);
    }

    @GetMapping("/search_human/{partOfName}")
    public ResponseEntity<List<HumanDto>> searchHumans(@PathVariable String partOfName) {
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
    public ResponseEntity<HumanFullDto> update(@RequestBody HumanCreateDto humanCreateDto){
        HumanFullDto newHumanFullDto = humanService.changeHumanInfo(humanCreateDto);
        return new ResponseEntity<>(newHumanFullDto, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NoSuchHumanException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

}
