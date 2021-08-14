package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.userDto.UserDto;
import ru.gafarov.Family.dto.userDto.UserFullDto;
import ru.gafarov.Family.dto.userDto.UserMaxDto;
import ru.gafarov.Family.dto.userDto.UserRegisterDto;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.model.Role;
import ru.gafarov.Family.model.User;

public class UserConverter {

    public static User toUser(UserRegisterDto userRegisterDto) throws RegisterException{
        User user = new User();
        user.setId(userRegisterDto.getId());
        try {
        user.setUsername(userRegisterDto.getUsername().trim());
        } catch (NullPointerException e){
            throw new RegisterException("username is required");
        }
        try {
            user.setPhone(userRegisterDto.getPhone());
        } catch (NullPointerException e){
            throw new RegisterException("phone is required");
        }

        try {
            user.setEmail(userRegisterDto.getEmail().trim());
        } catch (NullPointerException e){
            throw new RegisterException("email is required");
        }
        try {
            user.setPassword(userRegisterDto.getPassword().trim());
        } catch (NullPointerException e){
            throw new RegisterException("password is required");
        }
        return user;
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    public static UserFullDto toFullUserDto(User updatedUser) {
        return UserFullDto.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .username(updatedUser.getUsername())
                .phone(updatedUser.getPhone())
                .roles(updatedUser.getRoles().stream()
                        .map(Role::getName).toArray(String[]::new)
                ).build();
    }

    public static UserMaxDto toUserMaxDto(User user){
        return UserMaxDto.builder()
            .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .password(user.getPassword())
                .status(user.getStatus().toString())
                .roles((user.getRoles().stream().map(Role::getName).toArray(String[]::new)))
                .build();
    }
}
