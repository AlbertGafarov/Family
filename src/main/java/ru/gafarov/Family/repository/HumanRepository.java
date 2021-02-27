package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gafarov.Family.model.Human;

public interface HumanRepository extends JpaRepository<Human, Long> {
}
