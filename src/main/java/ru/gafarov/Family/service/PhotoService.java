package ru.gafarov.Family.service;

import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.model.Photo;

public interface PhotoService {
    //public PhotoDto save(Photo photo);

    PhotoDto save(MultipartFile file);

    void saveFile(MultipartFile file, String newFileName);

    void test();
}
