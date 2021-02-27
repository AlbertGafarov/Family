package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.userDto.UserDto;
import ru.gafarov.Family.dto.userDto.UserRegisterDto;
import ru.gafarov.Family.model.User;

public class UserConverter {

    public static User toUser(UserRegisterDto userRegisterDto){
        User user = new User();
        user.setId(userRegisterDto.getId());
        user.setUsername(userRegisterDto.getUsername().trim());
        user.setPhone(userRegisterDto.getPhone());
        user.setEmail(userRegisterDto.getEmail().trim());
        if(userRegisterDto.getPassword()!=null) {
            user.setPassword(userRegisterDto.getPassword().trim());
        }
        return user;
    }

    public static UserDto toUserDto(User user) {

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setPhone(user.getPhone());
        userDto.setEmail(user.getEmail());
        return userDto;

    }
}
