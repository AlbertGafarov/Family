package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.userDto.*;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User findByUsername(String username);
    User register(User user);
    UserDto register(UserCreateDto userCreateDto) throws RegisterException;
    User findById(Long id);
    void deleteById(Long id);
    Long getMyId(String token);
    User changeUserInfo(User me, UserCreateDto userCreateDto);
    List<User> searchPeople(String partOfName);
    User findMe(String bearerToken);

    void delete(User user);

}
