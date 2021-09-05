package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gafarov.Family.model.Story;

import java.util.List;

public interface StoryRepository extends JpaRepository<Story, Long> {
    @Query(value = "select f.* from stories f join stories_humans p on f.id = p.story_id join humans h on h.id = p.human_id where h.id = ?1 "
            , nativeQuery = true)
    List<Story> getStoryByHumanId(Long id);
}