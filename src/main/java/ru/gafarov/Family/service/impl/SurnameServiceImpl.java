package ru.gafarov.Family.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.exception_handling.NoSuchSurnameException;
import ru.gafarov.Family.model.Surname;
import ru.gafarov.Family.repository.SurnameRepository;
import ru.gafarov.Family.service.SurnameService;

import java.util.Optional;

@Service
public class SurnameServiceImpl implements SurnameService {

    @Autowired
    SurnameRepository surnameRepository;

    @Override
    public Surname getSurname(Long id) {
        Optional<Surname> opt = surnameRepository.findById(id);
        Surname surname;
        if (opt.isPresent()){
            surname = opt.get();
        } else {
            throw new NoSuchSurnameException("не найдена фамилия с id: " + id);
        }
        return surname;
    }
}
