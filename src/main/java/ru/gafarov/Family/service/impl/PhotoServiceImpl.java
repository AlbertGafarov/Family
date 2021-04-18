package ru.gafarov.Family.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReaderSpi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.exception_handling.PhotoFileException;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.repository.PhotoRepository;
import ru.gafarov.Family.service.PhotoService;

import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Value(value = "${path.root}")
    private String ROOT;

    @Value(value = "${path.photo}")
    private String PHOTOPATH;

    @Autowired
    PhotoRepository photoRepository;

    @Override
    public PhotoDto save(MultipartFile file, Date photoDate) throws IOException {

        Photo photo = new Photo();
        String imageResolution; // разрешение изображения

//Создать папки, если их нет:
        Path root = Paths.get(ROOT);
        Path photoFolder = Paths.get(ROOT + PHOTOPATH);

        if(!Files.exists(photoFolder)){
            if(!Files.exists(root)){
                Files.createDirectory(root);
                System.out.println(root + " created");
            }
            Files.createDirectory(photoFolder);
            System.out.println(photoFolder + " created");
        }


        String fileName = file.getOriginalFilename();
        long size = file.getSize();

        String exp = ""; // расширение для файла
        if (fileName != null && fileName.contains(".")) {
            exp = fileName.substring(fileName.lastIndexOf("."));
            if (!(exp.toUpperCase(Locale.ENGLISH).equals(".JPG") || exp.toUpperCase(Locale.ENGLISH).equals(".JPEG"))){
                throw new IIOException("Not a JPEG file");
            }
        }
        photo.setName(fileName);
        photo.setPath(PHOTOPATH);
        photo.setCreated(new Date());
        photo.setStatus(Status.ACTIVE);
        photo.setPhotoDate(photoDate);
        Photo savedPhoto = photoRepository.save(photo);

        String newFileName = ROOT + savedPhoto.getPath() + savedPhoto.getId() + exp;

        System.out.println(newFileName);
        try {
            //Сохранить файл
            saveFile(file, newFileName);
        } catch (IOException e) {
            //Удалить строчку из таблицы, если файл не удалось сохранить в файловой системе.
            photoRepository.delete(savedPhoto);
            throw e;
        }
// Считываем изображение из сохраненного файла:
        BufferedImage image = readFromFile(newFileName);
        //Метаданные изображения не используются пока
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(new File(newFileName));
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        }
        assert metadata != null;
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                System.out.format("[%s] - %s = %s",
                        directory.getName(), tag.getTagName(), tag.getDescription());
            }
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    System.err.format("ERROR: %s", error);
                }
            }
        }
        imageResolution = image.getWidth() + "x" + image.getHeight();

        return new PhotoDto(photo.getId(),fileName,size,imageResolution, photoDate);
    }

    @Override
    public void saveFile(MultipartFile file, String newFileName) throws IOException {
        try (
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFileName))
                ){
            byte[] bytes = file.getBytes();
            outputStream.write(bytes);
        }
    }

    @Override
    public BufferedImage readFromFile(String fileName) throws IOException{
        ImageReader r = new JPEGImageReader(new JPEGImageReaderSpi());
        r.setInput(new FileImageInputStream(new File(fileName)));
        BufferedImage bi = r.read(0, new ImageReadParam());
        ((FileImageInputStream) r.getInput()).close();
        return bi;
    }

    @Override
    public Photo getPhoto(Long id) {
        Optional<Photo> optional = photoRepository.findById(id);
        if(optional.isEmpty()){
            throw new PhotoFileException("Фото с id " + id + " не найдено");
        }
        return optional.get();
    }

    @Override
    public File getPhotoFile(Long id) {
        Photo photo = getPhoto(id);
        return new File(ROOT + photo.getPath() + id + ".jpg");
    }
}
