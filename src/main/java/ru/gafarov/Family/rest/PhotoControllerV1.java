package ru.gafarov.Family.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.dto.photoDto.ChangePhotoDto;
import ru.gafarov.Family.dto.photoDto.FullPhotoDto;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.exception_handling.NoSuchHumanException;
import ru.gafarov.Family.exception_handling.PhotoFileException;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.service.PhotoService;

import javax.imageio.IIOException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoControllerV1 {

    @Autowired
    PhotoService photoService;


    @PostMapping("") //Загрузить файл фото
    public ResponseEntity<PhotoDto> addPhoto(@RequestParam("file") MultipartFile file, @RequestParam("date") String photoDateStr) throws ParseException {
        if (file==null){
            throw new PhotoFileException("There is no file");
        }
        Date photoDate =new SimpleDateFormat("yyyy-MM-dd").parse(photoDateStr);
        PhotoDto photoDto;
        try {
            photoDto = photoService.save(file, photoDate);
        } catch (IIOException e) {
            e.printStackTrace();
            throw new PhotoFileException(e.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
            throw new PhotoFileException("Error when saved file");
        }
        return new ResponseEntity<>(photoDto, HttpStatus.OK);
    }

    @GetMapping("/{id}")     //Получить файл фото
    public void getPhotoFile(@PathVariable(name = "id") Long id, HttpServletResponse response){
        Photo photo = photoService.getPhoto(id);
        File photoFile = photoService.getPhotoFile(id);
        Path path = photoFile.toPath();
        if(Files.exists(path)){
            response.setHeader("Content-Disposition", "attachment;filename=" + photo.getName());
            response.setContentType("image/jpeg");
        } else {
            throw new PhotoFileException("не найден файл " + path);
        }
        try {
            Files.copy(path, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @GetMapping("/{id}/info") // Получение полной информации о фото
    public ResponseEntity<FullPhotoDto> getPhoto(@PathVariable(name = "id") Long id){
        FullPhotoDto fullPhotoDto = photoService.getFullPhotoDto(id);
        return new ResponseEntity<>(fullPhotoDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/info") //Изменить информацию о фото, добавить людей, изображенных на фото
    public ResponseEntity<FullPhotoDto> changePhotoInfo(@RequestBody ChangePhotoDto changePhotoDto){
        FullPhotoDto fullPhotoDto = photoService.changePhoto(changePhotoDto);

        return new ResponseEntity<>(fullPhotoDto, HttpStatus.OK);
    }

    @GetMapping("/humans/{id}") // Получить список фото, на которых изображен человек
    public ResponseEntity<List<PhotoDto>> getPhotoByHumanId(@PathVariable Long id){
        List<PhotoDto> photoDtoList = photoService.getPhotoByHumanId(id);
        return new ResponseEntity<>(photoDtoList, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData> handlerException(PhotoFileException exception){
            MessageIncorrectData message = new MessageIncorrectData();
            message.setInfo(exception.getMessage());
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NoSuchHumanException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

}


