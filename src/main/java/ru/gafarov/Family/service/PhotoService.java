package ru.gafarov.Family.service;

import ru.gafarov.Family.dto.photoDto.PhotoCreateDto;
import ru.gafarov.Family.exception_handling.NotFoundException;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PhotoService {

    Photo findById(Long id);
    File getPhotoFile(Long id);
    Photo save(PhotoCreateDto photoCreateDto, User me) throws IOException;
    Photo changePhoto(PhotoCreateDto photoCreateDto, User me) throws NotFoundException;
    List<Photo> getPhotoByHumanId(Long id);
    void deleteById(Long id, User me);

}
