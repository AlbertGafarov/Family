package ru.gafarov.Family.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.converter.UserConverter;
import ru.gafarov.Family.dto.userDto.UserDto;
import ru.gafarov.Family.dto.userDto.UserRegisterDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.exception_handling.NoSuchUserException;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.service.UserService;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestControllerV1 {
    @Autowired
    private UserService userService;

    @GetMapping(value = "/userinfo")
    public ResponseEntity<UserDto> getUserInfo(@RequestHeader(value = "Authorization") String bearerToken) {
        User user = userService.findMe(bearerToken);
        return new ResponseEntity<>(UserConverter.toUserDto(user), HttpStatus.OK);
    }

    @PutMapping("/change_info")
    public ResponseEntity<UserDto> update(@RequestBody UserRegisterDto userRegisterDto
            , @RequestHeader(value = "Authorization") String bearerToken){
        if(userRegisterDto.getPassword()!=null){
            throw new NoSuchUserException("You can't change password here. Try again without password");
        }
        User me = userService.findMe(bearerToken);
        UserDto newUserDto = userService.changeUserInfo(me, userRegisterDto);
        return new ResponseEntity<>(newUserDto, HttpStatus.OK);
    }

    @GetMapping("/search_user/{partOfName}")
    public ResponseEntity<List<UserDto>> searchPeople(@PathVariable String partOfName
            , @RequestHeader(value = "Authorization") String bearerToken) {
        if (partOfName.length() < 3) {
            throw new NoSuchUserException("You entered part of name too short. Please enter more than 2 symbols");
        }
        if (partOfName.length() > 15) {
            throw new NoSuchUserException("Yoy entered part of too long. Please enter less than 15 symbols");
        }
        User me = userService.findMe(bearerToken);
        List<User> listOfPeople = userService.searchPeople(partOfName);

        if (listOfPeople.isEmpty()) {
            throw new NoSuchUserException("There is no people like: " + partOfName);
        }

        List<UserDto> listOfPeopleDto = listOfPeople.stream()
                .filter(user -> !user.equals(me))
                .map(UserConverter::toUserDto)
                .sorted(Comparator.comparing(UserDto::getUsername))
                .collect(Collectors.toList());

        return new ResponseEntity<>(listOfPeopleDto, HttpStatus.OK);
    }

    @DeleteMapping("") // Удаление самого себя
    public ResponseEntity<Map<String,String>> deleteUser(@RequestHeader(value = "Authorization") String bearerToken){
        User user = userService.findMe(bearerToken);
        userService.delete(user);
        Map<String,String> map = new HashMap<>();
        map.put("message","User successfully deleted");
        return new ResponseEntity<>(map, HttpStatus.OK);

    }

    @ExceptionHandler
    public ResponseEntity<MessageIncorrectData>handleException(NoSuchUserException exception){
        MessageIncorrectData message = new MessageIncorrectData();
        message.setInfo(exception.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }
}
