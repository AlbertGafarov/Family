package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gafarov.Family.model.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    }
