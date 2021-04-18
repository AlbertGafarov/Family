package ru.gafarov.Family.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.humanDto.HumanShortDto;
import ru.gafarov.Family.dto.photoDto.ChangePhotoDto;
import ru.gafarov.Family.dto.photoDto.FullPhotoDto;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.PhotoService;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoConverter {

    @Autowired
    HumanService humanService;

    @Autowired
    PhotoService photoService;

    public PhotoDto toPhotoDto(Photo photo){
        PhotoDto photoDto = new PhotoDto();
        photoDto.setId(photo.getId());
        photoDto.setName(photo.getName());
        photoDto.setPhotoDate(photo.getPhotoDate());
        return photoDto;
    }
    public FullPhotoDto toFullPhotoDto(Photo photo){
        FullPhotoDto fullPhotoDto = new FullPhotoDto();
        fullPhotoDto.setId(photo.getId());
        fullPhotoDto.setName(photo.getName());
        if(photo.getPhotoDate()!=null){
            fullPhotoDto.setPhoto_date(HumanConverter.dateFormat.format(photo.getPhotoDate().getTime()));
        }
        List<HumanShortDto> humanShortDtos = photo.getHumans()
                .stream()
                .map(HumanConverter::toHumanShortDto)
                .collect(Collectors.toList());
        fullPhotoDto.setHumans_on_photo(humanShortDtos);
        return fullPhotoDto;
    }

    public Photo toPhoto(ChangePhotoDto changePhotoDto) throws NoSuchHumanException { // изменение информации о фото
        Photo photo = photoService.getPhoto(changePhotoDto.getId());
        photo.setId(changePhotoDto.getId());
        photo.setUpdated(new Date());

        if(changePhotoDto.getName()!=null) {
            photo.setName(changePhotoDto.getName().trim());
        }
        if(changePhotoDto.getPhotoDate()!=null){
            photo.setPhotoDate(changePhotoDto.getPhotoDate());
        }
        if(changePhotoDto.getHumans_id()!=null){
            photo.setHumans(Arrays.stream(changePhotoDto.getHumans_id())
                    .map(id -> humanService.getHuman(id))
                    .collect(Collectors.toList()));
        }

        return photo;
    }
}
