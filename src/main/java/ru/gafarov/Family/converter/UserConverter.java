package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.userDto.*;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.model.Role;
import ru.gafarov.Family.model.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class UserConverter {
    public static final DateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy'T'hh.mm.ss");

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    public static UserMaxDto toUserMaxDto(User user){
        return UserMaxDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(user.getPassword())
                .status(user.getStatus().toString())
                .roles((user.getRoles().stream().map(Role::getName).toArray(String[]::new)))
                .updated(user.getUpdated())
                .created(user.getCreated())
                .build();
    }
}
