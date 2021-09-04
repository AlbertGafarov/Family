package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gafarov.Family.model.Human;

import java.util.List;

public interface HumanRepository extends JpaRepository<Human, Long> {

    @Query(value = "select h.* from humans h " +
            "left join surnames s on h.surname_id = s.id where " +
            "lower(name) like %?1% " +
            "or replace(replace(lower(name),'ь',''),'ъ','') like %?2% " +
            "or replace(lower(name),'''','') like %?3% " +
            "or lower(surname) like %?1% " +
            "or replace(replace(lower(surname),'ь',''),'ъ','') like %?2% " +
            "or replace(lower(surname),'''','') like %?3% "
            , nativeQuery = true)
    List<Human> searchHuman(String partOfName, String partOfNameLowerCyrilic, String partOfNameLowerLatin);

    @Query(value = "select distinct p.* " +
            "from humans h " +
            "join humans_parents hp " +
            "on h.id = hp.human_id " +
            "join humans p " +
            "on hp.parent_id = p.id " +
            "where h.id = ?1"
            , nativeQuery = true)
    List<Human> getParents(Long id);
}
