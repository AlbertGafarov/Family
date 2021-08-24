package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gafarov.Family.model.Surname;

import java.util.List;

public interface SurnameRepository extends JpaRepository<Surname, Long> {

    @Query(value = "select * from Surnames u " +
            "where lower(u.surname) like %?1% " +
            "or replace(replace(lower(u.surname),'ь',''),'ъ','') like %?2% " +
            "or replace(lower(u.surname),'''','') like %?3% " +
            "or lower(u.sur_alias_1) like %?1% " +
            "or replace(replace(lower(u.sur_alias_1),'ь',''),'ъ','') like %?2% " +
            "or replace(lower(u.sur_alias_1),'''','') like %?3% " +
            "or lower(u.sur_alias_2) like %?1% " +
            "or replace(replace(lower(u.sur_alias_2),'ь',''),'ъ','') like %?2% " +
            "or replace(lower(u.sur_alias_2),'''','') like %?3% "
            , nativeQuery = true)
    List<Surname> searchSurnames(String partOfName, String partOfNameLowerCyrillic, String partOfNameLowerLatin);
}

