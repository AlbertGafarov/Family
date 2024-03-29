package ru.gafarov.Family.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.gafarov.Family.dto.userDto.AuthenticationRequestDto;
import ru.gafarov.Family.dto.userDto.UserTokenDto;
import ru.gafarov.Family.exception_handling.MessageIncorrectData;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.security.jwt.JwtTokenProvider;
import ru.gafarov.Family.service.UserService;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestControllerV1 { // Контроллер для аутентификации

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login") //метод для аутентификации
    public ResponseEntity<UserTokenDto> login(@RequestBody AuthenticationRequestDto requestDto){

        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);
            if(user == null){
                log.info("in login(): User with username: " + username + "not found");
                throw new UsernameNotFoundException("User with username: " + username + "not found");
            }
            String token = jwtTokenProvider.createToken(username, user.getRoles());

            UserTokenDto userTokenDto = new UserTokenDto(username, token);

            return ResponseEntity.ok(userTokenDto);
        } catch (AuthenticationException e) {
            log.info("in login(): Invalid username or password");
            throw new BadCredentialsException("Invalid username or password");
        }
    }

//    @ExceptionHandler
//    public ResponseEntity<MessageIncorrectData> handleException(UsernameNotFoundException exception){
//        MessageIncorrectData data = new MessageIncorrectData();
//        data.setInfo(exception.getMessage());
//        return new ResponseEntity<>(data, HttpStatus.UNAUTHORIZED);
//    }
}
