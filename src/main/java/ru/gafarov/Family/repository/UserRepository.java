package ru.gafarov.Family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);
    @Query(value = "select * from Users u " +
            "where lower(u.username) like %?1% " +
            "or replace(replace(lower(u.username),'ь',''),'ъ','') like %?2% " +
            "or replace(lower(u.username),'''','') like %?3% " +
            "or replace(substring( lower(u.email) from 1 for position ('@' in email)-1),'''','') like %?3%"
            , nativeQuery = true)
    List<User> searchPeople(String partOfName, String partOfNameLowerCyrillic, String partOfNameLowerLatin);
}
