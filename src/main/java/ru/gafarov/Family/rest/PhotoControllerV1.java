package ru.gafarov.Family.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gafarov.Family.dto.photoDto.PhotoDto;
import ru.gafarov.Family.model.Photo;
import ru.gafarov.Family.service.PhotoService;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/photos")
public class PhotoControllerV1 {

    @Autowired
    PhotoService photoService;

    @PostMapping("")
    public ResponseEntity<PhotoDto> addPhoto(@RequestParam("fileo") MultipartFile file){
        PhotoDto photoDto = photoService.save(file);
        return new ResponseEntity<>(photoDto, HttpStatus.OK);
    }
    @GetMapping("")
    public void show(){
        photoService.test();
    }

}
