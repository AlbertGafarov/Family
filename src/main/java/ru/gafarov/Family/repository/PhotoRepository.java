package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gafarov.Family.model.Photo;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query(value = "select f.* from photos f join photo_humans p on f.id = p.photo_id join humans h on h.id = p.human_id where h.id = ?1 "
        , nativeQuery = true)
    List<Photo> getPhotoByHumanId(Long id);
}
