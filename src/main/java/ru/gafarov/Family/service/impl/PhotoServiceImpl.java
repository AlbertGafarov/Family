package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.converter.PhotoConverter;
import ru.gafarov.Family.dto.photoDto.PhotoCreateDto;
import ru.gafarov.Family.exception_handling.ConflictException;
import ru.gafarov.Family.exception_handling.ForbiddenException;
import ru.gafarov.Family.exception_handling.NotFoundException;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.repository.PhotoRepository;
import ru.gafarov.Family.service.HumanService;
import ru.gafarov.Family.service.PhotoService;
import ru.gafarov.Family.service.UserService;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PhotoServiceImpl implements PhotoService {

    private final String ROOT;
    private final String PHOTOPATH;
    private final PhotoRepository photoRepository;
    private final HumanService humanService;
    private final UserService userService;

    public PhotoServiceImpl(@Value(value = "${path.root}") String ROOT
            , @Value(value = "${path.photo}") String PHOTOPATH
            , @Autowired PhotoRepository photoRepository, @Autowired HumanService humanService, @Autowired UserService userService) {
        this.ROOT = ROOT;
        this.PHOTOPATH = PHOTOPATH;
        this.photoRepository = photoRepository;
        this.humanService = humanService;
        this.userService = userService;
    }

    @Override
    public Photo findById(Long id) {

        Photo photo = photoRepository.findById(id).orElse(null);
        if (photo == null) {
            throw new NotFoundException("Not found photo with id " + id);
        }
        return photo;
    }

    @Override
    public Photo save(PhotoCreateDto photoCreateDto, User me) throws IOException { // Добавление инфо о фото в базу данных + сохранение файл в файловом хранилище

        Path root = Paths.get(ROOT);
        Path photoFolder = Paths.get(ROOT + PHOTOPATH);

        if(!Files.exists(photoFolder)){ // Создать папки, если их нет
            if(!Files.exists(root)){
                Files.createDirectory(root);
                log.info("created folder {}", root);
            }
            Files.createDirectory(photoFolder);
            log.info("created folder {}", photoFolder);
        }

        String fileName = photoCreateDto.getFile().getOriginalFilename();

        String exp = ""; // расширение для файла
        if (fileName != null && fileName.contains(".")) {
            exp = fileName.substring(fileName.lastIndexOf("."));
            if (!(exp.equalsIgnoreCase(".JPG") || exp.equalsIgnoreCase(".JPEG"))){
                throw new IIOException("Not a JPEG file");
            }
        }

        Photo photo = Photo.builder()
                .name(fileName)
                .path(PHOTOPATH)
                .photoDate(photoCreateDto.getPhotoDate())
                .humansOnPhoto(Arrays.stream(photoCreateDto.getHumans_id())
                        .map(humanService::findById)
                        .peek(a -> {
                            if(!a.getStatus().equals(Status.ACTIVE)){
                                throw new NotFoundException("Not found human with id " + a.getId() );
                            }
                        }).collect(Collectors.toList()))
                .created(new Date())
                .updated(new Date())
                .status(Status.ACTIVE)
                .author(me)
                .size(photoCreateDto.getFile().getSize())
                .build();

        photo = photoRepository.save(photo);
        String newFileName = ROOT + photo.getPath() + photo.getId() + exp;
        log.info("in save(). filename = {}", fileName);

        try {
            saveFile(photoCreateDto.getFile(), newFileName); //Сохранить файл
        } catch (IOException e) {
            photoRepository.delete(photo); //Удалить строчку из таблицы, если файл не удалось сохранить в файловой системе.
            throw e;
        }

        BufferedImage image = readFromFile(newFileName); // Считываем изображение из сохраненного файла:
        photo.setHeight(image.getHeight());
        photo.setWidth(image.getWidth());

        return photoRepository.save(photo);
    }

    public void saveFile(MultipartFile file, String newFileName) throws IOException { // Сохранить файл в хранилище
        try (
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newFileName))
                ){
            byte[] bytes = file.getBytes();
            outputStream.write(bytes);
        }
    }

    public BufferedImage readFromFile(String fileName) throws IOException{ // Считать изображение из файла
        return ImageIO.read(new File(fileName));
    }

    @Override
    public File getPhotoFile(Long id) { // Получить файл
        Photo photo = findById(id);
        return new File(ROOT + photo.getPath() + id + ".jpg");
    }

    @Override
    public Photo changePhoto(PhotoCreateDto photoCreateDto, User me) throws NotFoundException { //Изменить информацию о фото

        Photo photo = photoRepository.findById(photoCreateDto.getId()).orElse(null);
        if (photo == null){
            throw new NotFoundException("photo with id " + photoCreateDto.getId() + " not found");
        }
        if (!photo.getStatus().equals(Status.ACTIVE) && me != null){
            log.info("in changePhoto(). Not found exception, because status = {}", photo.getStatus());
            throw new NotFoundException("Not found photo with id: " + photo.getId());
        }
        if(!photo.getAuthor().equals(me) && me!=null){
            throw new ForbiddenException("You are not author photo with id = " + photoCreateDto.getId() + ", so you cannot change this");
        }
        if (photoCreateDto.getName() != null){
            photo.setName(photoCreateDto.getName());
        }
        if (photoCreateDto.getPhotoDate() != null){
            photo.setPhotoDate(photoCreateDto.getPhotoDate());
        }
        if (photoCreateDto.getHumans_id() != null){
            photo.setHumansOnPhoto(Arrays.stream(photoCreateDto.getHumans_id())
                    .map(humanService::findById)
                    .peek(a -> {
                        if (a == null) {
                            throw new NotFoundException("one or more human on photo not found in database");
                        }
                        if (!a.getStatus().equals(Status.ACTIVE)){
                            log.info("in changePhoto(). One human on photo not ACTIVE");
                            throw new NotFoundException("one or more human on photo not found in database");
                        }
                    })
                    .collect(Collectors.toList()));
        }

        if(photoCreateDto.getAuthor_id()!=null && me == null){ //Изменить автора может только админ, админ передает: me = null
            User author = userService.findById(photoCreateDto.getAuthor_id());
            if (author == null){
                log.info("author not found with id = {}", photoCreateDto.getAuthor_id());
                throw new NotFoundException("author with id " + photoCreateDto.getAuthor_id() + "not found");
            }
            photo.setAuthor(author);
        }
        if(photoCreateDto.getStatus()!=null && me == null){ // Изменить статус может только админ, админ передает: me = null
            photo.setStatus(Status.valueOf(photoCreateDto.getStatus()));
        }
        photo.setUpdated(new Date());
        return photoRepository.save(photo);
    }

    @Override
    public List<Photo> getPhotoByHumanId(Long id) { //получить список фото для человека по id
        List<Photo> photoList = photoRepository.getPhotoByHumanId(id);
        log.info("photoList = {}", photoList.stream().map(PhotoConverter::toPhotoDto));
        return photoList;
    }

    @Override
    public void deleteById(Long id, User me) {
        Photo photo = findById(id);
        if(me != null){
            if (!photo.getStatus().equals(Status.ACTIVE)){
                log.info("in deleteById(). Not found exception, because status = {}", photo.getStatus());
                throw new NotFoundException("Not found photo with id: " + id);
            }
            else if (!photo.getAuthor().equals(me)){
                log.info("in deleteById(). You are not author photo with id = {}, so you cannot delete this", photo.getId());
                throw new ConflictException("You are not author photo with id:" + photo.getId() + ", so you cannot delete this");
            }
            photo.setStatus(Status.DELETED);
            photoRepository.save(photo); // Если удаляет пользователь, то запись помечается удаленной
        } else {
            File file =  new File(ROOT + photo.getPath() + id + photo.getName().substring(photo.getName().lastIndexOf(".")));
            log.info("file {} exist?: {}", file.getAbsoluteFile(), file.exists());
            if(file.delete()){
                photoRepository.deleteById(id); // Если удаляет админ, то запись удаляется из бд.
            } else {
                throw new ConflictException("some problem with deleting file");
            }
        }
    }
}