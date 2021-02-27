package ru.gafarov.Family.security.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.gafarov.Family.model.Role;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserFactory {
    public JwtUserFactory() {
    }
    public static JwtUser create(User user){
        return new JwtUser(
                        user.getId(),
                        user.getUsername(),
                        user.getPhone(),
                        user.getPassword(),
                        user.getEmail(),
                        user.getStatus().equals(Status.ACTIVE),
                        user.getUpdated(),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles())));



    }
    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles){
        return userRoles.stream()
                .map(role ->
                        new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
