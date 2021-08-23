package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.dto.humanDto.HumanMaxDto;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;
import ru.gafarov.Family.model.Human;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

public class HumanConverter {

    public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static HumanDto toHumanDto(Human human){
        HumanDto humanDto = HumanDto.builder()
                .id(human.getId())
                .gender(human.getGender())
                .name(human.getName())
                .patronim(human.getPatronim())
                .build();
        if(human.getBirthdate()!=null){
            humanDto.setBirthdate(dateFormat.format(human.getBirthdate().getTime()));
        }
        if(human.getDeathdate()!=null){
            humanDto.setDeathdate(dateFormat.format(human.getDeathdate().getTime()));
        }
        if(human.getSurname()!=null){
        humanDto.setSurname(human.getSurname().toString(human.getGender()));
        }
        if(human.getBirthplace()!=null) {
            humanDto.setBirthplaceShortDto(BirthplaceConverter.toBirthplaceShortDto(human.getBirthplace()));
        }
        return humanDto;
    }

    public static HumanShortDto toHumanShortDto(Human human){
        return HumanShortDto.builder()
                .id(human.getId())
                .surname(human.getSurname().toString(human.getGender()))
                .name(human.getName())
                .patronim(human.getPatronim())
                .build();
    }

    public static HumanFullDto toHumanFullDto(Human human){
        HumanFullDto humanFullDto = HumanFullDto.builder()
                .id(human.getId())
                .name(human.getName())
                .surname(human.getSurname().toString(human.getGender()))
                .patronim(human.getPatronim())
                .build();
        if(human.getBirthdate()!=null){
            humanFullDto.setBirthdate(dateFormat.format(human.getBirthdate().getTime()));
        }
        if(human.getDeathdate()!=null){
            humanFullDto.setDeathdate(dateFormat.format(human.getDeathdate().getTime()));
        }
        humanFullDto.setGender(human.getGender());
        humanFullDto.setParents(human.getParents()
                .stream()
                .map(HumanConverter::toHumanShortDto)
                .collect(Collectors.toList()));
        humanFullDto.setChildren(human.getChildren()
                .stream()
                .map(HumanConverter::toHumanShortDto)
                .collect(Collectors.toList()));
        if(human.getBirthplace()!=null) {
            humanFullDto.setBirthplaceShortDto(BirthplaceConverter.toBirthplaceShortDto(human.getBirthplace()));
        }

        return humanFullDto;
    }

    public static HumanMaxDto toHumanMaxDto(Human human) {

        HumanMaxDto humanMaxDto = HumanMaxDto.builder()
                .id(human.getId())
                .name(human.getName())
                .surname(human.getSurname().getSurname())
                .patronim(human.getPatronim())
                .birthplaceShortDto(BirthplaceConverter.toBirthplaceShortDto(human.getBirthplace()))
                .children(human.getChildren().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .parents(human.getParents().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .gender(human.getGender())
                .author(UserConverter.toUserDto(human.getAuthor()))
                .status(human.getStatus())
                .created(human.getCreated())
                .updated(human.getUpdated())
                .build();

        if(human.getBirthdate() != null) {
            humanMaxDto.setBirthdate(dateFormat.format(human.getBirthdate().getTime()));
        }
        if(human.getDeathdate() != null){
            humanMaxDto.setDeathdate(dateFormat.format(human.getDeathdate().getTime()));
        }
        return humanMaxDto;
    }

}