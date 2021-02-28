package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface HumanService {

    Human getHuman(Long id);

    HumanDto getHumanDto(Long id);

    List<HumanDto> getAllHumansDto();

    HumanFullDto getHumanFullDto(Long id);

    HumanFullDto addHuman(HumanCreateDto humanCreateDto);

    List<Human> searchHuman(String partOfName);

    HumanFullDto changeHumanInfo(HumanCreateDto humanCreateDto);
}
