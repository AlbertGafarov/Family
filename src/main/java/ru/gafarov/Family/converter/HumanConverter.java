package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.dto.humanDto.HumanMaxDto;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;
import ru.gafarov.Family.model.Human;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.stream.Collectors;

public class HumanConverter {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static HumanDto toHumanDto(Human human){
        return HumanDto.builder()
                .id(human.getId())
                .gender(human.getGender())
                .name(human.getName())
                .surname(getSurnameString(human))
                .patronim(human.getPatronim())
                .birthdate(getDateString(human.getBirthdate()))
                .deathdate(getDateString(human.getDeathdate()))
                .birthplaceShortDto(BirthplaceConverter.toBirthplaceShortDto(human.getBirthplace()))
                .build();
    }

    public static HumanShortDto toHumanShortDto(Human human){
        return HumanShortDto.builder()
                .id(human.getId())
                .surname(getSurnameString(human))
                .name(human.getName())
                .patronim(human.getPatronim())
                .build();
    }

    public static HumanFullDto toHumanFullDto(Human human){

        return HumanFullDto.builder()
                .id(human.getId())
                .name(human.getName())
                .surname(getSurnameString(human))
                .patronim(human.getPatronim())
                .gender(human.getGender())
                .children(human.getChildren().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .parents(human.getParents().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .birthplaceShortDto(BirthplaceConverter.toBirthplaceShortDto(human.getBirthplace()))
                .birthdate(getDateString(human.getBirthdate()))
                .deathdate(getDateString(human.getDeathdate()))
                .build();
    }

    public static HumanMaxDto toHumanMaxDto(Human human) {

        return HumanMaxDto.builder()
                .id(human.getId())
                .name(human.getName())
                .surname(getSurnameString(human))
                .patronim(human.getPatronim())
                .birthplaceShortDto(BirthplaceConverter.toBirthplaceShortDto(human.getBirthplace()))
                .children(human.getChildren().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .parents(human.getParents().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .gender(human.getGender())
                .birthdate(getDateString(human.getBirthdate()))
                .deathdate(getDateString(human.getDeathdate()))
                .author(UserConverter.toUserDto(human.getAuthor()))
                .status(human.getStatus())
                .created(human.getCreated())
                .updated(human.getUpdated())
                .build();
    }

    private static String getSurnameString(Human human){
        return human.getSurname() == null ? null : human.getSurname().toString(human.getGender());
    }
    private static String getDateString(Calendar calendar) {
            return calendar == null ? null : dateFormat.format(calendar.getTime());
    }
}