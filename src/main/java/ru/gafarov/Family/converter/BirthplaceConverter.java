package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.birthplaceDto.BirthplaceDto;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceMaxDto;
import ru.gafarov.Family.dto.birthplaceDto.BirthplaceShortDto;
import ru.gafarov.Family.model.Birthplace;

public class BirthplaceConverter {

    public static BirthplaceShortDto toBirthplaceShortDto(Birthplace birthplace) {
        return BirthplaceShortDto.builder()
                .id(birthplace.getId())
                .birthplace(birthplace.getBirthplace())
                .build();
    }

    public static BirthplaceDto toBirthplaceDto(Birthplace birthplace) {
        return BirthplaceDto.builder()
                .id(birthplace.getId())
                .birthplace(birthplace.getBirthplace())
                .guid(birthplace.getGuid())
                .build();
    }

    public static BirthplaceMaxDto toBirthplaceMaxDto(Birthplace birthplace) {
        return BirthplaceMaxDto.builder()
                .id(birthplace.getId())
                .birthplace(birthplace.getBirthplace())
                .guid(birthplace.getGuid())
                .author(UserConverter.toUserDto(birthplace.getAuthor()))
                .status(birthplace.getStatus())
                .updated(birthplace.getUpdated())
                .created(birthplace.getCreated())
                .build();
    }
}
