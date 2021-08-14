package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.userDto.*;
import ru.gafarov.Family.model.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    User register(User user);
    UserDto register(UserRegisterDto userRegisterDto);
    List<User> getAll();
    User findById(Long id);
    void deleteById(Long id);
    Long getMyId(String token);
    UserDto changeUserInfo(User me, UserChangeInfoDto userChangeInfoDto);
    List<User> searchPeople(String partOfName);
    User findMe(String bearerToken);

    void delete(User user);

    UserFullDto changeUserInfo(UserRegisterDto userRegisterDto);
    UserMaxDto changeUserInfo(UserChangeInfoDto userChangeInfoDto);
}
