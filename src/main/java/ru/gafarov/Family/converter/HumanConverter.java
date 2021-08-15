package ru.gafarov.Family.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.humanDto.*;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.service.BirthplaceService;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.SurnameService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HumanConverter {

    @Autowired
    HumanService humanService;

    @Autowired
    BirthplaceService birthplaceService;

    @Autowired
    SurnameService surnameService;

    public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static HumanDto toHumanDto(Human human){
        HumanDto humanDto = new HumanDto();
        humanDto.setId(human.getId());
        if(human.getBirthdate()!=null){
            humanDto.setBirthdate(dateFormat.format(human.getBirthdate().getTime()));
        }
        humanDto.setGender(human.getGender());
        if(human.getDeathdate()!=null){
            humanDto.setDeathdate(dateFormat.format(human.getDeathdate().getTime()));
        }
        humanDto.setName(human.getName());
        humanDto.setPatronim(human.getPatronim());
        if(human.getSurname()!=null){
        humanDto.setSurname(human.getSurname().toString(human.getGender()));
        }
        if(human.getBirthplace()!=null) {
            humanDto.setBirthplace(human.getBirthplace().toString());
        }
        return humanDto;
    }

    public static HumanShortDto toHumanShortDto(Human human){
        HumanShortDto humanShortDto = new HumanShortDto();
        humanShortDto.setId(human.getId());
        humanShortDto.setSurname(human.getSurname().toString(human.getGender()));
        humanShortDto.setName(human.getName());
        humanShortDto.setPatronim(human.getPatronim());
        return humanShortDto;
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
            humanFullDto.setBirthplace(human.getBirthplace().toString());
        }

        return humanFullDto;
    }

    public static HumanMaxDto toHumanMaxDto(Human human) {

        HumanMaxDto humanMaxDto = HumanMaxDto.builder()
                .id(human.getId())
                .name(human.getName())
                .surname(human.getSurname().getSurname())
                .patronim(human.getPatronim())
                .birthplace(human.getBirthplace().getBirthplace())
                .children(human.getChildren().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .parents(human.getParents().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()))
                .author(UserConverter.toUserDto(human.getAuthor()))
                .status(human.getStatus().toString())
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

    public Human toHuman(HumanCreateDto humanCreateDto){

        Human human = Human.builder()
                .name(humanCreateDto.getName().trim())
                .birthdate(humanCreateDto.getBirthdate())
                .deathdate(humanCreateDto.getDeathdate())
                .patronim(humanCreateDto.getPatronim())
                .gender(humanCreateDto.getGender())
                .surname(surnameService.getSurname(humanCreateDto.getSurname_id()))
                .build();

        List<Human> parents =  Arrays.stream(humanCreateDto.getParents_id())
                .mapToObj(i-> humanService.getHuman((long) i))
                .peek(a -> {
                    if(a.getBirthdate() != null && humanCreateDto.getBirthdate() !=null){
                        if(a.getBirthdate().after(humanCreateDto.getBirthdate())){
                            throw new NoSuchHumanException("parent cannot be younger then human");
                        }
                    }
                })
                .collect(Collectors.toList());

        human.setParents(parents);

        List<Human> children =  Arrays.stream(humanCreateDto.getChildren_id())
                .mapToObj(i-> humanService.getHuman((long) i))
                .peek(a -> {
                    if(a.getBirthdate() != null && humanCreateDto.getBirthdate() !=null) {
                        if (a.getBirthdate().before(humanCreateDto.getBirthdate())) {
                            throw new NoSuchHumanException("child cannot be elder then human");
                        }
                    }
                })
                .collect(Collectors.toList());

        human.setChildren(children);

        List<Human> previousSurnames =  Arrays.stream(humanCreateDto.getPrevious_surnames_id())
                .mapToObj(i-> humanService.getHuman((long) i))
                .collect(Collectors.toList());

        human.setBirthplace(birthplaceService.findById(humanCreateDto.getBirthplace_id()));

        return human;

    }

}