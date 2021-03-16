package ru.gafarov.Family.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.converter.PhotoConverter;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.repository.PhotoRepository;
import ru.gafarov.Family.service.PhotoService;
import java.io.*;
import java.util.Date;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Value(value = "${root}")
    private String ROOT;

    @Autowired
    PhotoRepository photoRepository;

    @Override
    public PhotoDto save(MultipartFile file) {
        Photo photo = new Photo();
        String fileName = file.getOriginalFilename();

        String exp = "";
        if (fileName != null && fileName.contains(".")) {
            exp = fileName.substring(fileName.lastIndexOf("."));
        }
        photo.setName(fileName);
        photo.setPath("photos\\");
        photo.setCreated(new Date());
        photo.setStatus(Status.ACTIVE);
        Photo savedPhoto = photoRepository.save(photo);

        String newFileName = ROOT + savedPhoto.getPath() + savedPhoto.getId() + exp;

        System.out.println(newFileName);
        saveFile(file, newFileName);


        return PhotoConverter.toPhotoDto(photo);
    }

    @Override
    public void saveFile(MultipartFile file, String newFileName) {
        try (
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFileName))
                ){
            byte[] bytes = file.getBytes();
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void test() {
        System.out.println(ROOT);
    }
}
