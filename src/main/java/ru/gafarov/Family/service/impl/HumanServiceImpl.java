package ru.gafarov.Family.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.converter.HumanConverter;
import ru.gafarov.Family.dto.humanDto.HumanDto;
import ru.gafarov.Family.dto.humanDto.HumanFullDto;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.model.Human;
import ru.gafarov.Family.repository.HumanRepository;
import ru.gafarov.Family.service.HumanService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HumanServiceImpl implements HumanService {

    @Autowired
    HumanRepository humanRepository;

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
}
