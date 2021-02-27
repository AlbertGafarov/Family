package ru.gafarov.Family.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.service.HumanService;

import java.util.List;

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

}
