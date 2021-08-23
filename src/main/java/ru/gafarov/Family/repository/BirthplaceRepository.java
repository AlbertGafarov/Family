package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gafarov.Family.model.Birthplace;

import java.util.List;

public interface BirthplaceRepository extends JpaRepository<Birthplace, Long> {

    @Query(value = "select * from Birthplaces u " +
            "where lower(u.birthplace) like %?1% " +
            "or replace(replace(lower(u.birthplace),'ь',''),'ъ','') like %?2% " +
            "or replace(lower(u.birthplace),'''','') like %?3% "
            , nativeQuery = true)
    List<Birthplace> searchBirthplaces(String partOfName, String partOfNameLowerCyrillic, String partOfNameLowerLatin);
}

