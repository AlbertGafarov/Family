package ru.gafarov.Family.dto.photoDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties
public class PhotoDto {
    private Long id;
    private String name;
    private long size;
    private String imageResolution;
    private Date photoDate;

    public PhotoDto(Long id, String name, long size, String imageResolution, Date photoDate) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.imageResolution = imageResolution;
        this.photoDate = photoDate;
    }

    public PhotoDto() {
    }
}
