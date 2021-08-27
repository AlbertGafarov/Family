package ru.gafarov.Family.converter;

import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.dto.photoDto.PhotoFullDto;
import ru.gafarov.Family.dto.photoDto.PhotoMaxDto;
import ru.gafarov.Family.model.Photo;

import java.util.stream.Collectors;

public class PhotoConverter {

    public static PhotoDto toPhotoDto(Photo photo){
        return PhotoDto.builder()
                .id(photo.getId())
                .name(photo.getName())
                .photoDate(photo.getPhotoDate())
                .build();
    }
    public static PhotoFullDto toPhotoFullDto(Photo photo){
        PhotoFullDto photoFullDto = PhotoFullDto.builder()
                .id(photo.getId())
                .name(photo.getName())
                .photoDate(photo.getPhotoDate())
                .height(photo.getHeight())
                .width(photo.getWidth())
                .size(photo.getSize())
                .build();
        if(photo.getHumansOnPhoto()!=null){
            photoFullDto.setHumansOnPhoto(photo.getHumansOnPhoto().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()));
            }
        return photoFullDto;
    }

    public static PhotoMaxDto toPhotoMaxDto(Photo photo) {
        PhotoMaxDto photoMaxDto = PhotoMaxDto.builder()
                .id(photo.getId())
                .name(photo.getName())
                .photoDate(photo.getPhotoDate())
                .height(photo.getHeight())
                .width(photo.getWidth())
                .size(photo.getSize())
                .author(UserConverter.toUserDto(photo.getAuthor()))
                .status(photo.getStatus())
                .created(photo.getCreated())
                .updated(photo.getUpdated())
                .build();
        if (photo.getHumansOnPhoto() != null) {
            photoMaxDto.setHumansOnPhoto(photo.getHumansOnPhoto().stream().map(HumanConverter::toHumanShortDto).collect(Collectors.toList()));
        }
        return photoMaxDto;
    }
}
