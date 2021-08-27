package ru.gafarov.Family.dto.photoDto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
public class PhotoDto {

    private Long id;
    private String name;
    private Date photoDate;
}
