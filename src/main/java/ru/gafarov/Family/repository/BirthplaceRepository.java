package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gafarov.Family.model.Birthplace;

public interface BirthplaceRepository extends JpaRepository<Birthplace, Long> {
}
