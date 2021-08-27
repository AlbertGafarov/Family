package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.birthplaceDto.BirthplaceCreateDto;
import ru.gafarov.Family.model.Birthplace;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface BirthplaceService {

    Birthplace findById(Long id);
    Birthplace addBirthplace(BirthplaceCreateDto birthplaceCreateDto, User me);
    Birthplace changeBirthplace(BirthplaceCreateDto birthplaceCreateDto, User me);
    void deleteById(Long id, User me);
    List<Birthplace> searchBirthplaces(String partOfName);
}
