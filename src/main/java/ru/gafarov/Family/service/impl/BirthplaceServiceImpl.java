package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceCreateDto;
import ru.gafarov.Family.exception_handling.ConflictException;
import ru.gafarov.Family.exception_handling.ForbiddenException;
import ru.gafarov.Family.exception_handling.NotFoundException;
import ru.gafarov.Family.model.Birthplace;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.repository.BirthplaceRepository;
import ru.gafarov.Family.service.BirthplaceService;
import ru.gafarov.Family.service.Transcript;
import ru.gafarov.Family.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class BirthplaceServiceImpl implements BirthplaceService {

    private final BirthplaceRepository birthplaceRepository;
    private final Transcript transcript;
    private final UserService userService;

    public BirthplaceServiceImpl(@Autowired BirthplaceRepository birthplaceRepository, @Autowired Transcript transcript, @Autowired UserService userService) {
        this.birthplaceRepository = birthplaceRepository;
        this.transcript = transcript;
        this.userService = userService;
    }

    @Override
    public Birthplace findById(Long id) {
        Birthplace birthplace = birthplaceRepository.findById(id).orElse(null);
        if(birthplace == null){
            throw new NotFoundException("Not found birthplace with id: " + id);
        }
        return birthplace;
    }

    @Override
    public Birthplace addBirthplace(BirthplaceCreateDto birthplaceCreateDto, User me) {
        Birthplace birthplace = Birthplace.builder()
                .birthplace(birthplaceCreateDto.getBirthplace())
                .author(me)
                .created(new Date())
                .updated(new Date())
                .status(Status.ACTIVE)
                .build();
        if(birthplaceCreateDto.getGuid()!=null){
            birthplace.setGuid(UUID.fromString(birthplaceCreateDto.getGuid()));
        }
        return birthplaceRepository.save(birthplace);
    }

    @Override
    public Birthplace changeBirthplace(BirthplaceCreateDto birthplaceCreateDto, User me) {
        Birthplace birthplace = birthplaceRepository.findById(birthplaceCreateDto.getId()).orElse(null);
        if (birthplace == null){
            throw new NotFoundException("birthplace with id " + birthplaceCreateDto.getId() + " not found");
        }
        if (!birthplace.getStatus().equals(Status.ACTIVE) && me != null){
            log.info("in changeBirthplace(). Not found exception, because status = {}", birthplace.getStatus());
            throw new NotFoundException("Not found birthplace with id: " + birthplace.getId());
        }
        if(!birthplace.getAuthor().equals(me) && me!=null){
            throw new ForbiddenException("You are not author birthplace with id = " + birthplaceCreateDto.getId() + ", so you cannot change this");
        }
        if(birthplaceCreateDto.getBirthplace()!=null){
            birthplace.setBirthplace(birthplaceCreateDto.getBirthplace());
        }
        if(birthplaceCreateDto.getGuid()!=null){
            birthplace.setGuid(UUID.fromString(birthplaceCreateDto.getGuid()));
        }
        if(birthplaceCreateDto.getAuthor_id()!=null && me == null){ //Изменить автора может только админ, админ передает: me = null
            User author = userService.findById(birthplaceCreateDto.getAuthor_id());
            if (author == null){
                log.info("author not found with id = {}", birthplaceCreateDto.getAuthor_id());
                throw new NotFoundException("author with id " + birthplaceCreateDto.getAuthor_id() + "not found");
            }
            birthplace.setAuthor(author);
        }
        if(birthplaceCreateDto.getStatus()!=null && me == null){ // Изменить статус может только админ, админ передает: me = null
            birthplace.setStatus(Status.valueOf(birthplaceCreateDto.getStatus()));
        }
        birthplace.setUpdated(new Date());
        return birthplaceRepository.save(birthplace);
    }

    @Override
    public void deleteById(Long id, User me) {
        Birthplace birthplace = findById(id);
        if(me != null){
            if (!birthplace.getStatus().equals(Status.ACTIVE)){
                log.info("in deleteById(). Not found exception, because status = {}", birthplace.getStatus());
                throw new NotFoundException("Not found birthplace with id: " + id);
            }
            else if (!birthplace.getAuthor().equals(me)){
                log.info("in deleteById(). You are not author birthplace with id = {}, so you cannot delete this", birthplace.getId());
                throw new ConflictException("You are not author birthplace with id:" + birthplace.getId() + ", so you cannot delete this");
            }
            birthplace.setStatus(Status.DELETED);
            birthplaceRepository.save(birthplace); // Если удаляет пользователь, то запись помечается удаленной
        } else {
            birthplaceRepository.deleteById(id); // Если удаляет админ, то запись удаляется из бд.
        }
    }

    @Override
    public List<Birthplace> searchBirthplaces(String partOfName) {
        String partOfNameLowerCyrillic = transcript.trimSoftAndHardSymbol(transcript.toCyrillic(partOfName));
        String partOfNameLowerLatin = transcript.trimSoftAndHardSymbol(transcript.toLatin(partOfName));
        log.info("IN toCyrilic result: {}", partOfNameLowerCyrillic);
        log.info("IN toLatin result: {}", partOfNameLowerLatin);

        return birthplaceRepository.searchBirthplaces(partOfName, partOfNameLowerCyrillic, partOfNameLowerLatin);
    }
}
