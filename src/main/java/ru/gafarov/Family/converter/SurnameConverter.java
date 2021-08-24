package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.surnameDto.SurnameDto;
import ru.gafarov.Family.dto.surnameDto.SurnameMaxDto;
import ru.gafarov.Family.model.Surname;

public class SurnameConverter {

    public static SurnameDto toSurnameDto(Surname surname) {
        return SurnameDto.builder()
                .id(surname.getId())
                .declension(surname.getDeclension())
                .surname(surname.getSurname())
                .surnameAlias1(surname.getSurnameAlias1())
                .surnameAlias2(surname.getSurnameAlias2())
                .build();
    }

    public static SurnameMaxDto toSurnameMaxDto(Surname surname) {
        return SurnameMaxDto.builder()
                .id(surname.getId())
                .surname(surname.getSurname())
                .declension(surname.getDeclension())
                .surnameAlias1(surname.getSurnameAlias1())
                .surnameAlias2(surname.getSurnameAlias2())
                .author(UserConverter.toUserDto(surname.getAuthor()))
                .status(surname.getStatus())
                .updated(surname.getUpdated())
                .created(surname.getCreated())
                .build();
    }
}
