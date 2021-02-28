package ru.gafarov.Family.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.exception_handling.NoSuchBirthplaceException;
import ru.gafarov.Family.model.Birthplace;
import ru.gafarov.Family.repository.BirthplaceRepository;
import ru.gafarov.Family.service.BirthplaceService;

import java.util.Optional;

@Service
public class BirthplaceServiceImpl implements BirthplaceService {

    @Autowired
    BirthplaceRepository birthplaceRepository;

    @Override
    public Birthplace findById(Long id) {
        Optional<Birthplace> opt = birthplaceRepository.findById(id);
        if(opt.isEmpty()){
            throw new NoSuchBirthplaceException("Not found birthplace with id: " + id);
        }
        return opt.get();
    }
}
