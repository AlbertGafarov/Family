package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.exception_handling.NoSuchSurnameException;
import ru.gafarov.Family.exception_handling.NoSuchUserException;
import ru.gafarov.Family.model.*;
import ru.gafarov.Family.repository.HumanRepository;
import ru.gafarov.Family.service.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HumanServiceImpl implements HumanService {

    private final Transcript transcript;
    private final HumanRepository humanRepository;
    private final HumanConverter humanConverter;
    private final BirthplaceService birthplaceService;
    private final SurnameService surnameService;
    private final UserService userService;

    @Autowired
    public HumanServiceImpl(Transcript transcript, HumanRepository humanRepository, HumanConverter humanConverter, BirthplaceService birthplaceService, SurnameService surnameService, UserService userService) {
        this.transcript = transcript;
        this.humanRepository = humanRepository;
        this.humanConverter = humanConverter;
        this.birthplaceService = birthplaceService;
        this.surnameService = surnameService;
        this.userService = userService;
    }

    @Override
    public Human getHuman(Long id) {
        Human human;
        Optional<Human> opt = humanRepository.findById(id);
        if (opt.isPresent()){
            human = opt.get();
        } else {
            throw new NoSuchHumanException("Не найден человек с id: " + id);
        }

        return human;
    }

    @Override
    public HumanDto getHumanDto(Long id){
        return HumanConverter.toHumanDto(getHuman(id));
    }

    @Override
    public List<HumanDto> getAllHumansDto() {
        List<Human> humanList = humanRepository.findAll();
        return humanList.stream()
                .map(HumanConverter::toHumanDto)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public HumanFullDto getHumanFullDto(Long id) {
        return HumanConverter.toHumanFullDto(getHuman(id));
    }

    @Override
    public HumanFullDto addHuman(HumanCreateDto humanCreateDto, User me) {

        Calendar birthdate = humanCreateDto.getBirthdate();
        Calendar deathdate = humanCreateDto.getDeathdate();

        if (birthdate != null){
            if(birthdate.after(new Date())) {
                throw new NoSuchHumanException("birthdate cannot be earlier then today");
            }
        }
        if (deathdate != null){
            if(deathdate.after(new Date())) {
                throw new NoSuchHumanException("deathdate cannot be earlier then today");
            }
        }
        if(birthdate != null && deathdate != null){
            if(birthdate.after(deathdate)){
                throw new NoSuchHumanException("birthdate cannot be after deathdate");
            }
        }
        Human human = humanConverter.toHuman(humanCreateDto);
        human.setAuthor(me);
        human.setCreated(new Date());
        human.setStatus(Status.ACTIVE);
        Human addedHuman = humanRepository.save(human);
        return HumanConverter.toHumanFullDto(addedHuman);
    }

    @Override
    public List<Human> searchHuman(String partOfName) {

        String partOfNameLowerCyrillic = transcript.trimSoftAndHardSymbol(transcript.toCyrillic(partOfName));
        String partOfNameLowerLatin = transcript.trimSoftAndHardSymbol(transcript.toLatin(partOfName));
        log.info("IN toCyrilic result: {}", partOfNameLowerCyrillic);
        log.info("IN toLatin result: {}", partOfNameLowerLatin);

        return humanRepository.searchHuman(partOfName, partOfNameLowerCyrillic, partOfNameLowerLatin);
    }

    @Override
    public Human changeHumanInfo(HumanCreateDto humanCreateDto, User me) throws NoSuchHumanException { //Изменить информацию о человеке

        Calendar birthdate = humanCreateDto.getBirthdate();
        Calendar deathdate = humanCreateDto.getDeathdate();

        if (birthdate != null){
            if(birthdate.after(new Date())) {
                throw new NoSuchHumanException("birthdate cannot be earlier then today");
            }
        }
        if (deathdate != null){
            if(deathdate.after(new Date())) {
                throw new NoSuchHumanException("deathdate cannot be earlier then today");
            }
        }
        if(birthdate != null && deathdate != null){
            if(birthdate.after(deathdate)){
                throw new NoSuchHumanException("birthdate cannot be after deathdate");
            }
        }

        Human human = humanRepository.findById(humanCreateDto.getId()).orElse(null);
        if(human==null){
            throw new NoSuchHumanException("Not found human with id = " + humanCreateDto.getId());
        }
        if(!(human.getAuthor().equals(me) || me == null)){
            throw new NoSuchHumanException("You are not author for this human. Please ask admin for change info");
        }
        String x;
        Long y;
        Calendar d;
        int[] childrenIdArray;
        int[] parentIdArray;
        int[] previousSurnameIdArray;

        if((x = humanCreateDto.getName())!=null){
            human.setName(x);
        }
        if((x = humanCreateDto.getPatronim())!=null){
            human.setPatronim(x);
        }
        if((humanCreateDto.getGender())!=null){
            human.setGender(humanCreateDto.getGender());
        }
        if((y = humanCreateDto.getBirthplace_id())!=null){
            Birthplace birthplace = birthplaceService.findById(y);
            human.setBirthplace(birthplace);
        }
        if((y = humanCreateDto.getSurname_id())!=null){
            Surname surname = surnameService.getSurname(y);
            human.setSurname(surname);
        }
        if((d = humanCreateDto.getBirthdate())!=null){
            human.setBirthdate(d);
        }
        if((d = humanCreateDto.getDeathdate())!=null){
            human.setDeathdate(d);
        }
        if((childrenIdArray = humanCreateDto.getChildren_id())!=null){
            List<Human> children = Arrays.stream(childrenIdArray).mapToObj(a -> humanRepository.findById((long) a).orElse(null)).peek(a -> {
                if(a == null){
                    throw new NoSuchHumanException("Cannot found one or more children with id from this array" + Arrays.toString(childrenIdArray));
                }
                if (a.getBirthdate()!=null && human.getBirthdate() != null){
                    if(a.getBirthdate().before(human.getBirthdate())){
                        throw new NoSuchHumanException("Child cannot be elder then human");
                    }
                }
            }).collect(Collectors.toList());

            human.setChildren(children);
        }
        if ((parentIdArray = humanCreateDto.getParents_id()) != null) {
            List<Human> parents = Arrays.stream(parentIdArray).mapToObj(a -> humanRepository.findById((long) a).orElse(null)).peek(a -> {
                if (a == null) {
                    throw new NoSuchHumanException("Cannot found one or more parents with id from this array" + Arrays.toString(parentIdArray));
                }
                if (a.getBirthdate() != null && human.getBirthdate() != null) {
                    if (a.getBirthdate().after(human.getBirthdate())) {
                        throw new NoSuchHumanException("parent cannot be younger then human");
                    }
                }
            }).collect(Collectors.toList());
            human.setParents(parents);
        }
        if((previousSurnameIdArray = humanCreateDto.getPrevious_surnames_id())!=null){
            try {
                List<Surname> previousSurnames = Arrays.stream(previousSurnameIdArray).mapToObj(a -> surnameService.getSurname((long) a)).collect(Collectors.toList());
            } catch (NoSuchSurnameException e){
                throw new NoSuchHumanException(e.getMessage());
            }
        }
        if((y = humanCreateDto.getAuthor_id()) != null){
            User author;
            try {
                author = userService.findById(y);
            } catch (NoSuchUserException e){
                throw new NoSuchHumanException(e.getMessage());
            }
            human.setAuthor(author);
        }

        human.setUpdated(new Date());

        return humanRepository.save(human);
    }

    @Override
    public void deleteById(Long id) throws EmptyResultDataAccessException {
        humanRepository.deleteById(id);
    }
}
