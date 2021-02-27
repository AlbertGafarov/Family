package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;
import ru.gafarov.Family.model.Human;

import java.util.stream.Collectors;

public class HumanConverter {

    public static HumanDto toHumanDto(Human human){
        HumanDto humanDto = new HumanDto();
        humanDto.setId(human.getId());
        humanDto.setBirthdate(human.getBirthdate());
        humanDto.setGender(human.getGender());
        humanDto.setDeathdate(human.getDeathdate());
        humanDto.setName(human.getName());
        humanDto.setPatronim(human.getPatronim());
        if(human.getSurname()!=null){
        humanDto.setSurname(human.getSurname().toString());
        }
        if(human.getBirthplace()!=null) {
            humanDto.setBirthplace(human.getBirthplace().toString());
        }
        return humanDto;
    }

    public static HumanShortDto toHumanShortDto(Human human){
        HumanShortDto humanShortDto = new HumanShortDto();
        humanShortDto.setId(human.getId());
        humanShortDto.setName(human.getName());
        humanShortDto.setPatronim(human.getPatronim());
        return humanShortDto;
    }

    public static HumanFullDto toHumanFullDto(Human human){
        HumanFullDto humanFullDto = new HumanFullDto();
        humanFullDto.setId(human.getId());
        humanFullDto.setName(human.getName());
        humanFullDto.setSurname(human.getSurname().toString());
        humanFullDto.setPatronim(human.getPatronim());
        humanFullDto.setBirthdate(human.getBirthdate());
        humanFullDto.setDeathdate(human.getDeathdate());
        humanFullDto.setGender(human.getGender());
        humanFullDto.setParents(human.getParents()
                .stream()
                .map(HumanConverter::toHumanShortDto)
                .collect(Collectors.toList()));
        humanFullDto.setChildren(human.getChildren()
                .stream()
                .map(HumanConverter::toHumanShortDto)
                .collect(Collectors.toList()));

        return humanFullDto;
    }

}
