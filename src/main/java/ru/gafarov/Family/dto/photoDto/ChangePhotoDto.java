package ru.gafarov.Family.dto.photoDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@JsonIgnoreProperties
@Data
public class ChangePhotoDto {
    private Long id;
    private String name;
    private Date photoDate;
    private Long[] humans_id;
}
