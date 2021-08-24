package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.surnameDto.SurnameCreateDto;
import ru.gafarov.Family.model.Surname;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface SurnameService {

    Surname changeSurname(SurnameCreateDto surnameCreateDto, User me);
    Surname findById(Long id);
    Surname addSurname(SurnameCreateDto surnameCreateDto, User me);
    void deleteById(Long id, User me);
    List<Surname> searchSurnames(String partOfName);
}
