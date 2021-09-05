package ru.gafarov.Family.service;

import org.springframework.dao.EmptyResultDataAccessException;
import ru.gafarov.Family.dto.userDto.*;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface UserService {

    List<User> getAll();
    User findByUsername(String username);
    User register(UserCreateDto userCreateDto);
    User findById(Long id);
    User changeUserInfo(User me, UserCreateDto userCreateDto);
    List<User> searchPeople(String partOfName);
    User findMe(String bearerToken);
    void deleteById(Long id, User me) throws EmptyResultDataAccessException;
}
