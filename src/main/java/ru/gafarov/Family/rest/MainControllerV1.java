package ru.gafarov.Family.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class MainControllerV1 {

    @GetMapping("/exit")
    public ResponseEntity<Map<String,String>> exit(){
        System.out.println("Приложение отключено администратором");
        System.exit(0);
        return new ResponseEntity<>(new HashMap<>(){{put("status", "Good Buy!");}}, HttpStatus.ACCEPTED);
    }

}
