package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gafarov.Family.model.Surname;

public interface SurnameRepository extends JpaRepository<Surname, Long> {
}
