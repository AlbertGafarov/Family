package ru.gafarov.Family.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.converter.PhotoConverter;
import ru.gafarov.Family.dto.photoDto.ChangePhotoDto;
import ru.gafarov.Family.dto.photoDto.FullPhotoDto;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.exception_handling.PhotoFileException;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.repository.PhotoRepository;
import ru.gafarov.Family.service.PhotoService;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final String ROOT;

    private final String PHOTOPATH;

    private final PhotoRepository photoRepository;

    private final PhotoConverter photoConverter;

    @Autowired
    public PhotoServiceImpl(@Value(value = "${path.root}") String ROOT
            , @Value(value = "${path.photo}") String PHOTOPATH
            , PhotoRepository photoRepository
            , PhotoConverter photoConverter) {
        this.ROOT = ROOT;
        this.PHOTOPATH = PHOTOPATH;
        this.photoRepository = photoRepository;
        this.photoConverter = photoConverter;
    }

    @Override
    public PhotoDto save(MultipartFile file, Date photoDate) throws IOException { // Добавление инфо о фото в базу данных + сохранение файл в файловом хранилище

        Photo photo = Photo.builder().build();
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
            if (!(exp.equalsIgnoreCase(".JPG") || exp.equalsIgnoreCase(".JPEG"))){
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
    public void saveFile(MultipartFile file, String newFileName) throws IOException { // Сохранить файл в хранилище
        try (
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFileName))
                ){
            byte[] bytes = file.getBytes();
            outputStream.write(bytes);
        }
    }

    @Override
    public BufferedImage readFromFile(String fileName) throws IOException{ // Считать изображение из файла
        return ImageIO.read(new File(fileName));
    }

    @Override
    public Photo getPhoto(Long id) { //Получить фото по id
        Optional<Photo> optional = photoRepository.findById(id);
        if(optional.isEmpty()){
            throw new PhotoFileException("Фото с id " + id + " не найдено");
        }
        return optional.get();
    }

    @Override
    public File getPhotoFile(Long id) { // Получить файл
        Photo photo = getPhoto(id);
        return new File(ROOT + photo.getPath() + id + ".jpg");
    }

    @Override
    public FullPhotoDto changePhoto(ChangePhotoDto changePhotoDto) throws NoSuchHumanException { //Изменить информацию о фото
        Photo photo = photoConverter.toPhoto(changePhotoDto);
        Photo savedPhoto = photoRepository.save(photo);
        return photoConverter.toFullPhotoDto(savedPhoto);
    }

    @Override
    public FullPhotoDto getFullPhotoDto(Long id) { // Получить полную DTO фото по id
        return photoConverter.toFullPhotoDto(getPhoto(id));
    }

    @Override
    public List<PhotoDto> getPhotoByHumanId(Long id) { //получить список фото для человека по id
        List<Photo> photoList = photoRepository.getPhotoByHumanId(id);
        return photoList.stream()
                .map(photoConverter::toPhotoDto)
                .collect(Collectors.toList());
    }
}
