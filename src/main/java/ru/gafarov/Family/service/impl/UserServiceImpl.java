package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.dto.userDto.UserCreateDto;
import ru.gafarov.Family.exception_handling.NotFoundException;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.model.Role;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.repository.RoleRepository;
import ru.gafarov.Family.repository.UserRepository;
import ru.gafarov.Family.security.jwt.JwtTokenProvider;
import ru.gafarov.Family.service.Transcript;
import ru.gafarov.Family.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final Transcript transcript;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder
            , JwtTokenProvider jwtTokenProvider, Transcript transcript) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.transcript = transcript;
    }


    @Override
    public User register(UserCreateDto userCreateDto) {
        User user = User.builder()
                .username(userCreateDto.getUsername())
                .password(passwordEncoder.encode(userCreateDto.getPassword()))
                .phone(userCreateDto.getPhone())
                .email(userCreateDto.getEmail())
                .roles(new ArrayList<>(){{add(roleRepository.findByName("ROLE_USER"));}})
                .status(Status.ACTIVE)
                .created(new Date())
                .updated(new Date())
                .build();
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        List<User> result = userRepository.findAll();
        log.info("IN getAll: {} users found", result);
        return result;
    }

    @Override
    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user: [{}] found by username: {}", result, username);
        return result;
    }

    @Override
    public User findById(Long id) {
        User result = userRepository.findById(id).orElse(null);
        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            throw new NotFoundException("There is no user with id " + id);
        }
        log.info("IN findById - user: {} found by id: {}", result.getUsername(), id);
        return result;
    }

    @Override
    public User findMe(String bearerToken) {
        String myName = jwtTokenProvider.getUserName(bearerToken.substring(7));
        User me = findByUsername(myName);
        log.info("IN getMyId user: {} found", me);
        return me;
    }

    @Override
    public User changeUserInfo(User user, UserCreateDto userCreateDto) {
        if(user == null) {
            if ((user = userRepository.findById(userCreateDto.getId()).orElse(null)) == null) {
                log.error("user not found");
                throw new NotFoundException("user not found");
            }
        }
        String x;
        Long y;
        if ((x = userCreateDto.getEmail()) != null) {
            user.setEmail(x);
        }
        if ((x = userCreateDto.getUsername()) != null) {
            user.setUsername(x);
        }
        if ((y = userCreateDto.getPhone()) != null) {
            user.setPhone(y);
        }
        if ((x = userCreateDto.getPassword()) != null) {
            user.setPassword(passwordEncoder.encode(x));
        }
        if ((x = userCreateDto.getStatus()) != null) {
            user.setStatus(Status.valueOf(x));
        }
        if (userCreateDto.getRoles() != null) {
            List<Role> roleList = new ArrayList<>();
            for (String roleName : userCreateDto.getRoles()) { // Если роль не начинается с префикса ROLE_, то добавить этот префикс
                if (!roleName.toUpperCase(Locale.ROOT).startsWith("ROLE_")) {
                    roleName = "ROLE_" + roleName;
                }
                Role role = roleRepository.findByName(roleName.trim().toUpperCase(Locale.ROOT));
                if (role != null) { // Если роль найдена, то добавить ее в список ролей
                    roleList.add(role);
                }
            }
            if (roleList.size() > 0) { // Если ни одна роль не подходит, либо нет ролей, то изменения не применяются
                user.setRoles(roleList);
            } else {
                log.error("cannot find at least one role from your list");
                throw new RegisterException("cannot find at least one role from your list");
            }
        }
        user.setUpdated(new Date());
        return userRepository.save(user);
    }

    @Override
    public List<User> searchPeople(String partOfName) {
        String partOfNameLowerCyrilic = transcript.trimSoftAndHardSymbol(transcript.toCyrillic(partOfName));
        String partOfNameLowerLatin = transcript.trimSoftAndHardSymbol(transcript.toLatin(partOfName));
        log.info("IN toCyrilic result: {}", partOfNameLowerCyrilic);
        log.info("IN toLatin reult: {}", partOfNameLowerLatin);

        return userRepository.searchPeople(partOfName, partOfNameLowerCyrilic, partOfNameLowerLatin);
    }


    @Override
    public void deleteById(Long id, User me) throws EmptyResultDataAccessException {
        if(me == null) {
            userRepository.deleteById(id);
            log.info("IN deleteById() - user with id: {} successfully deleted from database", id);
        } else {
            User user = findById(id);
            user.setStatus(Status.DELETED);
            user.setUpdated(new Date());
            userRepository.save(user);
            log.info("IN deleteById() - user with id: {} successfully marked deleted", id);
        }
    }
}
