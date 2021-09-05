package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.exception_handling.ConflictException;
import ru.gafarov.Family.exception_handling.ForbiddenException;
import ru.gafarov.Family.exception_handling.NotFoundException;
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
    private final BirthplaceService birthplaceService;
    private final SurnameService surnameService;
    private final UserService userService;

    @Autowired
    public HumanServiceImpl(Transcript transcript, HumanRepository humanRepository, BirthplaceService birthplaceService, SurnameService surnameService, UserService userService) {
        this.transcript = transcript;
        this.humanRepository = humanRepository;
        this.birthplaceService = birthplaceService;
        this.surnameService = surnameService;
        this.userService = userService;
    }

    @Override
    public Human findById(Long id) throws NotFoundException {
        Human human = humanRepository.findById(id).orElse(null);
        if (human == null){
            throw new NotFoundException("Not found human with id: " + id);
        }
        return human;
    }
    @Override
    public List<Human> getAllHumans() {
        return humanRepository.findAll();
    }

    @Override
    public Human addHuman(HumanCreateDto humanCreateDto, User me) {

        Calendar birthdate = humanCreateDto.getBirthdate();
        Calendar deathdate = humanCreateDto.getDeathdate();

        if (birthdate != null){
            if(birthdate.after(new Date())) {
                throw new ConflictException("birthdate cannot be earlier then today");
            }
        }
        if (deathdate != null){
            if(deathdate.after(new Date())) {
                throw new ConflictException("deathdate cannot be earlier then today");
            }
        }
        if(birthdate != null && deathdate != null){
            if(birthdate.after(deathdate)){
                throw new ConflictException("birthdate cannot be after deathdate");
            }
        }
        Human human = Human.builder()
                .name(humanCreateDto.getName())
                .surname(surnameService.findById(humanCreateDto.getSurname_id()))
                .patronim(humanCreateDto.getPatronim())
                .birthdate(birthdate)
                .deathdate(deathdate)
                .birthplace(birthplaceService.findById(humanCreateDto.getBirthplace_id()))
                .children(Arrays.stream(humanCreateDto.getChildren_id()).mapToObj(a -> findById((long) a)).peek(a -> {
                            if (a.getBirthdate() != null) {
                                if (a.getBirthdate().before(birthdate)) {
                                    throw new ConflictException("child cannot be elder than human");
                                }
                            }
                        }
                ).collect(Collectors.toList()))
                .parents(Arrays.stream(humanCreateDto.getParents_id()).mapToObj(a -> findById((long) a)).peek(a -> {
                    if (a.getBirthdate() != null) {
                        if (a.getBirthdate().after(birthdate)) {
                            throw new ConflictException("parent cannot be younger then human");
                        }
                    }
                }).collect(Collectors.toList()))
                .gender(humanCreateDto.getGender())
                .author(me)
                .created(new Date())
                .status(Status.ACTIVE)
                .updated(new Date())
                .build();
        return humanRepository.save(human);
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
    public List<Human> getBrothersAndSisters(Human human, int n) {
        if(n!=1) {
            return getFamily(human, n).get(0);
        } else {
            return getFamily(human,1).get(0).stream().filter(a -> !a.equals(human)).collect(Collectors.toList());
        }
    }

    private Map<Integer, List<Human>> getFamily(Human human, int n) {

        List<Human> ancestors = new ArrayList<>(); // Предки
        Map<Integer, List<Human>> family = new HashMap<>(); // Вся семья, где Integer - это поколение
        family.put(0, new ArrayList<>(){{ add(human);}}); // Нулевое поколение - это сам человек
        ancestors.add(human);

        for (int i = 1; i <= n; i++){
            ancestors = ancestors.stream()
                    .flatMap(a -> a.getParents().stream())
                    .filter(a -> a.getStatus().equals(Status.ACTIVE))
                    .distinct()
                    .collect(Collectors.toList());
            family.put(-i, ancestors); // добавляем в мап предков с индексом "-i", родители: -1, бабушки, дедушки -2, и.т.д
        }
        List<Human> descendants = ancestors.stream(). // Потомки
                flatMap(a -> a.getChildren().stream())
                .distinct()
                .filter(a -> !family.get(-(n-1)).contains(a)) // Отбираем, среди детей предков, только не прямых предков
                .collect(Collectors.toList());
        family.put(n - 1, descendants); // добавляем в мап предков с индексом "i", братья и сестры: 1, дяди и тети: 2, не родные бабушки, дедушки 3, и.т.д
        for (int i = n - 2; i >=0; i--){
            descendants = descendants.stream().flatMap(a -> a.getChildren().stream())
                    .filter(a -> a.getStatus().equals(Status.ACTIVE))
                    .distinct()
                    .collect(Collectors.toList());
            family.put(i, descendants);
        }
        return family;
    }

    @Override
    public Human changeHumanInfo(HumanCreateDto humanCreateDto, User me) throws NotFoundException { //Изменить информацию о человеке

        Human human = humanRepository.findById(humanCreateDto.getId()).orElse(null); // Получаем человека из БД
        if(human==null){ // Если человек не найден, бросаем эксепшн
            throw new NotFoundException("Not found human with id = " + humanCreateDto.getId());
        }
        if(!(human.getAuthor().equals(me) || me == null)){
            throw new ForbiddenException("You are not author for this human. Please ask admin for change info");
        }

        Calendar birthdate = humanCreateDto.getBirthdate();
        Calendar deathdate = humanCreateDto.getDeathdate();

        if (birthdate != null){
            if(birthdate.after(new Date())) {
                throw new ConflictException("birthdate cannot be earlier then today");
            }
        }
        if (deathdate != null){
            if(deathdate.after(new Date())) {
                throw new ConflictException("deathdate cannot be earlier then today");
            }
        }
        if(birthdate != null && deathdate != null){
            if(birthdate.after(deathdate)){
                throw new ConflictException("birthdate cannot be after deathdate");
            }
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
            Surname surname = surnameService.findById(y);
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
                    throw new NotFoundException("Cannot found one or more children with id from this array" + Arrays.toString(childrenIdArray));
                }
                if (a.getBirthdate()!=null && human.getBirthdate() != null){
                    if(a.getBirthdate().before(human.getBirthdate())){
                        throw new ConflictException("Child cannot be elder then human");
                    }
                }
            }).collect(Collectors.toList());

            human.setChildren(children);
        }
        if ((parentIdArray = humanCreateDto.getParents_id()) != null) {
            List<Human> parents = Arrays.stream(parentIdArray).mapToObj(a -> humanRepository.findById((long) a).orElse(null)).peek(a -> {
                if (a == null) {
                    throw new NotFoundException("Cannot found one or more parents with id from this array" + Arrays.toString(parentIdArray));
                }
                if (a.getBirthdate() != null && human.getBirthdate() != null) {
                    if (a.getBirthdate().after(human.getBirthdate())) {
                        throw new ConflictException("parent cannot be younger then human");
                    }
                }
            }).collect(Collectors.toList());
            human.setParents(parents);
        }
        if((previousSurnameIdArray = humanCreateDto.getPrevious_surnames_id())!=null){
            List<Surname> previousSurnames = Arrays.stream(previousSurnameIdArray).mapToObj(a -> surnameService.findById((long) a)).collect(Collectors.toList());
        }
        if((y = humanCreateDto.getAuthor_id()) != null) {
            User author= userService.findById(y);
            human.setAuthor(author);
        }
        if((x = humanCreateDto.getStatus()) != null){

            human.setStatus(Status.valueOf(x));
        }

        human.setUpdated(new Date());

        return humanRepository.save(human);
    }

    @Override
    public void deleteById(Long id, User me) {
        Human human = findById(id);
        if(me != null){
            if (!human.getStatus().equals(Status.ACTIVE)){
                log.info("in deleteById(). Not found exception, because status = {}", human.getStatus());
                throw new NotFoundException("Not found human with id: " + id);
            }
            else if (!human.getAuthor().equals(me)){
                log.info("in deleteById(). You are not author human with id = {}, so you cannot delete this", human.getId());
                throw new ConflictException("You are not author human with id:" + human.getId() + ", so you cannot delete this");
            }
            human.setStatus(Status.DELETED);
            humanRepository.save(human); // Если удаляет пользователь, то запись помечается удаленной
        } else {
            humanRepository.deleteById(id); // Если удаляет админ, то запись удаляется из бд.
        }
    }
}
