package ru.gafarov.Family.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.security.jwt.JwtUser;
import ru.gafarov.Family.security.jwt.JwtUserFactory;
import ru.gafarov.Family.service.UserService;

@Service
@Slf4j
public class JwtUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);
        if (user==null) {
            throw new UsernameNotFoundException("User with username "+ username+" not found");
        }
        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("IN loadByUserName - user with username: {} successfully loaded", username);
        return jwtUser;
    }
}
