package ru.gafarov.Family.converter;

import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.model.Photo;

@Service
public class PhotoConverter {
    public static PhotoDto toPhotoDto(Photo photo){
        PhotoDto photoDto = new PhotoDto();
        photoDto.setId(photo.getId());
        photoDto.setName(photo.getName());
        photoDto.setPhotoDate(photo.getPhotoDate());
        return photoDto;
    }
}
