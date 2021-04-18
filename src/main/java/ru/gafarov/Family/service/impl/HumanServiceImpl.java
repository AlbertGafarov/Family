package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.dto.humanDto.HumanCreateDto;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.repository.HumanRepository;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.Transcript;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HumanServiceImpl implements HumanService {

    private final Transcript transcript;

    private final HumanRepository humanRepository;

    private final HumanConverter humanConverter;

    @Autowired
    public HumanServiceImpl(Transcript transcript, HumanRepository humanRepository, HumanConverter humanConverter) {
        this.transcript = transcript;
        this.humanRepository = humanRepository;
        this.humanConverter = humanConverter;
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
    public HumanFullDto addHuman(HumanCreateDto humanCreateDto) {
        Human human = humanConverter.toHuman(humanCreateDto);
        human.setCreated(new Date());
        human.setStatus(Status.ACTIVE);
        Human addedHuman = humanRepository.save(human);
        return HumanConverter.toHumanFullDto(addedHuman);
    }

    @Override
    public List<Human> searchHuman(String partOfName) {

        String partOfNameLowerCyrilic = transcript.trimSoftAndHardSymbol(transcript.toCyrillic(partOfName));
        String partOfNameLowerLatin = transcript.trimSoftAndHardSymbol(transcript.toLatin(partOfName));
        log.info("IN toCyrilic result: {}", partOfNameLowerCyrilic);
        log.info("IN toLatin reult: {}", partOfNameLowerLatin);

        return humanRepository.searchHuman(partOfName, partOfNameLowerCyrilic, partOfNameLowerLatin);
    }

    @Override
    public HumanFullDto changeHumanInfo(HumanCreateDto humanCreateDto) { //Изменить информацию о человеке

        Human human = humanConverter.changeHumanInfo(humanCreateDto);
        human.setUpdated(new Date());
        Human updatedHuman = humanRepository.save(human);

        return HumanConverter.toHumanFullDto(updatedHuman);
    }
}
