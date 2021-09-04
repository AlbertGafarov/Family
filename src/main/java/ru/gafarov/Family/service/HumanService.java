package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface HumanService {

    Human findById(Long id);
    List<Human> getAllHumans();
    Human addHuman(HumanCreateDto humanCreateDto, User me);
    List<Human> searchHuman(String partOfName);
    List<Human> getBrothersAndSisters(Human human, int n);
    Human changeHumanInfo(HumanCreateDto humanCreateDto, User me);
    void deleteById(Long id, User me);
}
