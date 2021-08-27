package ru.gafarov.Family.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.converter.PhotoConverter;
import ru.gafarov.Family.dto.photoDto.PhotoCreateDto;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.dto.photoDto.PhotoFullDto;
import ru.gafarov.Family.exception_handling.*;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.PhotoService;
import ru.gafarov.Family.service.UserService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoControllerV1 {

    private final PhotoService photoService;
    private final UserService userService;

    public PhotoControllerV1(@Autowired PhotoService photoService, @Autowired UserService userService) {
        this.photoService = photoService;
        this.userService = userService;
    }


    @PostMapping("") // Загрузить файл фото
    public ResponseEntity<PhotoFullDto> addPhoto(@RequestParam("file") MultipartFile file
            , @RequestParam("date") String photoDateStr
            , @RequestParam("humans_id") String humans_id
            , @RequestHeader(value = "Authorization") String bearerToken) throws IOException {

        if (file==null){
            throw new BadRequestException("There is no file");
        }
        PhotoCreateDto photoCreateDto;
        try {
            photoCreateDto = PhotoCreateDto.builder()
                    .photoDate(new SimpleDateFormat("yyyy-MM-dd").parse(photoDateStr))
                    .file(file)
                    .humans_id(Arrays.stream(humans_id.split(","))
                        .map(Long::valueOf)
                        .toArray(Long[]::new))
                    .build();
        } catch (ParseException e) {
            throw new BadRequestException("date is not valid");
        }

        User me = userService.findMe(bearerToken);
        Photo photo = photoService.save(photoCreateDto, me);

        return new ResponseEntity<>(PhotoConverter.toPhotoFullDto(photo), HttpStatus.OK);
    }

    @GetMapping("/{id}") // Получить файл фото
    public void getPhotoFile(@PathVariable(name = "id") Long id, HttpServletResponse response
            , @RequestHeader(value = "Authorization") String bearerToken){
        Photo photo = photoService.findById(id);
        if (!photo.getStatus().equals(Status.ACTIVE)){
            throw new NotFoundException("photo not found with id " + id);
        }
        File photoFile = photoService.getPhotoFile(id);
        Path path = photoFile.toPath();
        if(Files.exists(path)){
            response.setHeader("Content-Disposition", "attachment;filename=" + photo.getName());
            response.setContentType("image/jpeg");
        } else {
            throw new NotFoundException("Not found file " + path);
        }
        try {
            Files.copy(path, response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @GetMapping("/{id}/info") // Получение информации о фото
    public ResponseEntity<PhotoFullDto> getPhoto(@PathVariable(name = "id") Long id
            , @RequestHeader(value = "Authorization") String bearerToken){

        Photo photo = photoService.findById(id);
        if (!photo.getStatus().equals(Status.ACTIVE)){
            throw new NotFoundException("Not found photo with id " + id);
        }
        return new ResponseEntity<>(PhotoConverter.toPhotoFullDto(photo), HttpStatus.OK);
    }

    @PutMapping("") //Изменить информацию о фото, добавить людей, изображенных на фото
    public ResponseEntity<PhotoFullDto> changePhotoInfo(@RequestBody PhotoCreateDto photoCreateDto
            , @RequestHeader(value = "Authorization") String bearerToken){
        User me = userService.findMe(bearerToken);
        Photo photo = photoService.changePhoto(photoCreateDto, me);
        return new ResponseEntity<>(PhotoConverter.toPhotoFullDto(photo), HttpStatus.OK);
    }

    @GetMapping("/humans/{id}") // Получить список фото, на которых изображен человек
    public ResponseEntity<List<PhotoDto>> getPhotoByHumanId(@PathVariable Long id
            , @RequestHeader(value = "Authorization") String bearerToken){
        List<Photo> photoList = photoService.getPhotoByHumanId(id);
        List<PhotoDto> photoDtoList = photoList.stream().filter(Objects::nonNull)
                .filter(a -> a.getStatus().equals(Status.ACTIVE))
                .map(PhotoConverter::toPhotoDto)
                .collect(Collectors.toList());
        if(photoDtoList.isEmpty()){
            throw new NotFoundException("There is no photo with human by id " + id);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(photoDtoList, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{id}") // Удалить фото
    public ResponseEntity<Map<String, String>> deletePhoto(@PathVariable Long id,  @RequestHeader(value = "Authorization") String bearerToken){

        User me = userService.findMe(bearerToken);
        photoService.deleteById(id, me);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(new HashMap<>(){{put("message", "photo with id " + id + " successfully deleted");}}, headers, HttpStatus.OK);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NotFoundException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(ConflictException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(BadRequestException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(ForbiddenException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}


