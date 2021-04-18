package ru.gafarov.Family.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;
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
        HumanFullDto humanFullDto = new HumanFullDto();
        humanFullDto.setId(human.getId());
        humanFullDto.setName(human.getName());
        humanFullDto.setSurname(human.getSurname().toString(human.getGender()));
        humanFullDto.setPatronim(human.getPatronim());
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

    public Human toHuman(HumanCreateDto humanCreateDto){
        Human human = new Human();
        human.setName(humanCreateDto.getName().trim());
        human.setBirthdate(humanCreateDto.getBirthdate());
        human.setDeathdate(humanCreateDto.getDeathdate());
        human.setPatronim(humanCreateDto.getPatronim());
        human.setGender(humanCreateDto.getGender());
        human.setSurname(surnameService.getSurname(humanCreateDto.getSurname_id()));

        List<Human> parents =  Arrays.stream(humanCreateDto.getParents_id())
                .mapToObj(i-> humanService.getHuman((long) i))
                .collect(Collectors.toList());

        human.setParents(parents);

        List<Human> children =  Arrays.stream(humanCreateDto.getChildren_id())
                .mapToObj(i-> humanService.getHuman((long) i))
                .collect(Collectors.toList());

        human.setChildren(children);

        List<Human> previousSurnames =  Arrays.stream(humanCreateDto.getPrevious_surnames_id())
                .mapToObj(i-> humanService.getHuman((long) i))
                .collect(Collectors.toList());

        human.setBirthplace(birthplaceService.findById((long) humanCreateDto.getBirthplace_id()));

        return human;

    }

    public Human changeHumanInfo(HumanCreateDto humanCreateDto){

        Human human = humanService.getHuman(humanCreateDto.getId());

        if(humanCreateDto.getName()!=null) {
            human.setName(humanCreateDto.getName().trim());
        }
        if(humanCreateDto.getBirthdate()!=null) {
            human.setBirthdate(humanCreateDto.getBirthdate());
        }
        if(humanCreateDto.getDeathdate()!=null) {
            human.setDeathdate(humanCreateDto.getDeathdate());
        }
        if(humanCreateDto.getPatronim()!=null) {
            human.setPatronim(humanCreateDto.getPatronim());
        }
        if(humanCreateDto.getGender()!=null) {
            human.setGender(humanCreateDto.getGender());
        }
        if(humanCreateDto.getSurname_id()!=null) {
            System.out.println("меняем фамилию");
            human.setSurname(surnameService.getSurname(humanCreateDto.getSurname_id()));
        } else {
            System.out.println("Не меняем фамилию " + humanCreateDto.getSurname_id());
        }

        if(humanCreateDto.getParents_id()!=null) {
            System.out.println("меняем родителелей");
            List<Human> parents = Arrays.stream(humanCreateDto.getParents_id())
                    .mapToObj(i -> humanService.getHuman((long) i))
                    .collect(Collectors.toList());

            human.setParents(parents);
        } else {
            System.out.println("не меняем родителей " + Arrays.toString(humanCreateDto.getParents_id()));
        }

        if(humanCreateDto.getChildren_id()!=null) {
            List<Human> children = Arrays.stream(humanCreateDto.getChildren_id())
                    .mapToObj(i -> humanService.getHuman((long) i))
                    .collect(Collectors.toList());

            human.setChildren(children);
        }

        if(humanCreateDto.getPrevious_surnames_id()!=null) {
            List<Human> previousSurnames = Arrays.stream(humanCreateDto.getPrevious_surnames_id())
                    .mapToObj(i -> humanService.getHuman((long) i))
                    .collect(Collectors.toList());
        }

        if(humanCreateDto.getBirthplace_id()!=null) {
            human.setBirthplace(birthplaceService.findById(humanCreateDto.getBirthplace_id()));
        }

        return human;
    }
}