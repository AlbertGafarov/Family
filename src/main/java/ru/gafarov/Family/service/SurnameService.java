package ru.gafarov.Family.service;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gafarov.Family.model.Surname;

public interface SurnameService {
    public Surname getSurname(Long id);
}
