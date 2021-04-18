package ru.gafarov.Family.service;

import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.model.Photo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public interface PhotoService {

    PhotoDto save(MultipartFile file, Date date) throws IOException;

    void saveFile(MultipartFile file, String newFileName) throws IOException;

    BufferedImage readFromFile(String fileName) throws IOException;

    Photo getPhoto(Long id);

    File getPhotoFile(Long id);
}
