package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.surnameDto.SurnameCreateDto;
import ru.gafarov.Family.exception_handling.ConflictException;
import ru.gafarov.Family.exception_handling.ForbiddenException;
import ru.gafarov.Family.exception_handling.NotFoundException;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.Surname;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.repository.SurnameRepository;
import ru.gafarov.Family.service.SurnameService;
import ru.gafarov.Family.service.Transcript;
import ru.gafarov.Family.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SurnameServiceImpl implements SurnameService {


    private final Transcript transcript;
    private final SurnameRepository surnameRepository;
    private final UserService userService;

    public SurnameServiceImpl(@Autowired Transcript transcript, @Autowired SurnameRepository surnameRepository, @Autowired UserService userService) {
        this.transcript = transcript;
        this.surnameRepository = surnameRepository;
        this.userService = userService;
    }

    @Override
    public Surname findById(Long id) throws NotFoundException {
        Surname surname = surnameRepository.findById(id).orElse(null);
        if (surname == null){
            throw new NotFoundException("Not found surname with id: " + id);
        }
        return surname;
    }

    @Override
    public Surname addSurname(SurnameCreateDto surnameCreateDto, User me) {

        Surname surname = Surname.builder()
                                .surname(surnameCreateDto.getSurname())
                                .surnameAlias1(surnameCreateDto.getSurnameAlias1())
                                .surnameAlias2(surnameCreateDto.getSurnameAlias1())
                                .declension(surnameCreateDto.getDeclension())
                                .author(me)
                                .created(new Date())
                                .updated(new Date())
                                .status(Status.ACTIVE)
                                .build();
        check(surname);
        return surnameRepository.save(surname);
    }

    private void check(Surname surname) throws ConflictException {
        long count = new ArrayList<String>() {{
            add(surname.getSurname());
            add(surname.getSurnameAlias1());
            add(surname.getSurnameAlias2());
        }}.stream().filter(Objects::nonNull)
                .distinct().count();

        if(count > 1) {
            throw new ConflictException("surname and aliases should be different");
        }
    }

    public Surname changeSurname(SurnameCreateDto surnameCreateDto, User me) {
        Surname surname = surnameRepository.findById(surnameCreateDto.getId()).orElse(null);
        if (surname == null){
            throw new NotFoundException("surname with id " + surnameCreateDto.getId() + " not found");
        }
        if (!surname.getStatus().equals(Status.ACTIVE) && me != null){
            log.info("in changeSurname(). Not found exception, because status = {}", surname.getStatus());
            throw new NotFoundException("Not found surname with id: " + surname.getId());
        }
        if(!surname.getAuthor().equals(me) && me!=null){
            throw new ForbiddenException("You are not author surname with id = " + surnameCreateDto.getId() + ", so you cannot change this");
        }
        if(surnameCreateDto.getSurname()!=null){
            surname.setSurname(surnameCreateDto.getSurname());
        }

        if(surnameCreateDto.getSurnameAlias1()!=null){
            surname.setSurnameAlias1(surnameCreateDto.getSurnameAlias1());
        }

        if(surnameCreateDto.getSurnameAlias2()!=null){
            surname.setSurnameAlias1(surnameCreateDto.getSurnameAlias2());
        }

        if(surnameCreateDto.getAuthor_id()!=null && me == null){ //Изменить автора может только админ, админ передает: me = null
            User author = userService.findById(surnameCreateDto.getAuthor_id());
            if (author == null){
                log.info("author not found with id = {}", surnameCreateDto.getAuthor_id());
                throw new NotFoundException("author with id " + surnameCreateDto.getAuthor_id() + "not found");
            }
            surname.setAuthor(author);
        }
        if(surnameCreateDto.getStatus()!=null && me == null){ // Изменить статус может только админ, админ передает: me = null
            surname.setStatus(Status.valueOf(surnameCreateDto.getStatus()));
        }
        surname.setUpdated(new Date());
        check(surname);
        return surnameRepository.save(surname);
    }

    @Override
    public void deleteById(Long id, User me) {
        Surname surname = findById(id);
        if(me != null){
            if (!surname.getStatus().equals(Status.ACTIVE)){
                log.info("in deleteById(). Not found exception, because status = {}", surname.getStatus());
                throw new NotFoundException("Not found surname with id: " + id);
            }
            else if (!surname.getAuthor().equals(me)){
                log.info("in deleteById(). You are not author surname with id = {}, so you cannot delete this", surname.getId());
                throw new ConflictException("You are not author surname with id:" + surname.getId() + ", so you cannot delete this");
            }
            surname.setStatus(Status.DELETED);
            surnameRepository.save(surname); // Если удаляет пользователь, то запись помечается удаленной
        } else {
            surnameRepository.deleteById(id); // Если удаляет админ, то запись удаляется из бд.
        }
    }

    @Override
    public List<Surname> searchSurnames(String partOfName) {
        String partOfNameLowerCyrillic = transcript.trimSoftAndHardSymbol(transcript.toCyrillic(partOfName));
        String partOfNameLowerLatin = transcript.trimSoftAndHardSymbol(transcript.toLatin(partOfName));
        log.info("IN toCyrilic result: {}", partOfNameLowerCyrillic);
        log.info("IN toLatin result: {}", partOfNameLowerLatin);

        return surnameRepository.searchSurnames(partOfName, partOfNameLowerCyrillic, partOfNameLowerLatin);
    }
}
