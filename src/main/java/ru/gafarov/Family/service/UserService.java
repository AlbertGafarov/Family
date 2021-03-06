package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.userDto.UserDto;
import ru.gafarov.Family.dto.userDto.UserRegisterDto;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User register(User user);
    UserDto register(UserRegisterDto userRegisterDto);
    List<User> getAll();
    User findById(Long id);
    void delete(Long id);
    Long getMyId(String token);
    UserDto changeUserInfo(User me, UserRegisterDto userRegisterDto);
    List<User> searchPeople(String partOfName);
    User findMe(String bearerToken);
}
