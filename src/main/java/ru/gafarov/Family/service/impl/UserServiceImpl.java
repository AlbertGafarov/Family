package ru.gafarov.Family.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gafarov.Family.converter.UserConverter;
import ru.gafarov.Family.dto.userDto.*;
import ru.gafarov.Family.exception_handling.NoSuchUserException;
import ru.gafarov.Family.exception_handling.RegisterException;
import ru.gafarov.Family.model.Role;
import ru.gafarov.Family.model.Status;
import ru.gafarov.Family.model.User;
import ru.gafarov.Family.repository.RoleRepository;
import ru.gafarov.Family.repository.UserRepository;
import ru.gafarov.Family.security.jwt.JwtTokenProvider;
import ru.gafarov.Family.service.Transcript;
import ru.gafarov.Family.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

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
    public User register(User user) {
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);
        user.setCreated(new Date());
        user.setUpdated(new Date());
        User registeredUser = userRepository.save(user);
        log.info("IN register - user: {} successfully registered", registeredUser);
        return registeredUser;
    }

    @Override
    public UserDto register(UserRegisterDto userRegisterDto) throws RegisterException {


        User user = UserConverter.toUser(userRegisterDto);
        User newUser = register(user);
        return UserConverter.toUserDto(newUser);
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
            throw new NoSuchUserException("There is no user with id " + id);
        }
        log.info("IN findById - user: {} found by id: {}", result.getUsername(), id);
        return result;
    }

    @Override
    public void deleteById(Long id) throws EmptyResultDataAccessException {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted", id);

    }

    @Override
    public Long getMyId(String token) {
        String myName = jwtTokenProvider.getUserName(token);
        User me = findByUsername(myName);
        log.info("IN getMyId user: {} found", me);
        return me.getId();
    }

    @Override
    public UserDto changeUserInfo(User me, UserChangeInfoDto userChangeInfoDto) {
        userChangeInfoDto.setId(me.getId());
        UserMaxDto userMaxDto = changeUserInfo(userChangeInfoDto);
        return UserDto.builder()
                .id(userMaxDto.getId())
                .username(userMaxDto.getUsername())
                .phone(userMaxDto.getPhone())
                .email(userMaxDto.getEmail())
                .build();
    }

    @Override
    public UserFullDto changeUserInfo(UserRegisterDto userRegisterDto) {
        User updatedUser = UserConverter.toUser(userRegisterDto);

        User currentUser = userRepository.findById(userRegisterDto.getId()).orElse(null);
        if (currentUser == null) {
            throw new NoSuchUserException("user not found");
        }
        updatedUser.setUpdated(new Date());
        if (userRegisterDto.getRoles() != null) {
            List<Role> roleList = Arrays.stream(userRegisterDto.getRoles())
                    .map(a -> roleRepository.findByName(a.toUpperCase(Locale.ROOT)))
                    .collect(Collectors.toList());
            updatedUser.setRoles(roleList);
        }
        updatedUser.setCreated(currentUser.getCreated());
        updatedUser.setStatus(currentUser.getStatus());
        updatedUser.setPassword(currentUser.getPassword());
        userRepository.save(updatedUser);

        return UserConverter.toFullUserDto(updatedUser);
    }

    @Override
    public UserMaxDto changeUserInfo(UserChangeInfoDto userChangeInfoDto) {
        User user = userRepository.findById(userChangeInfoDto.getId()).orElse(null);
        if (user == null) {
            log.error("user not found");
            throw new NoSuchUserException("user not found");
        }
        String x;
        Long y;
        if ((x = userChangeInfoDto.getEmail()) != null) {
            user.setEmail(x);
        }
        if ((x = userChangeInfoDto.getUsername()) != null) {
            user.setUsername(x);
        }
        if ((y = userChangeInfoDto.getPhone()) != null) {
            user.setPhone(y);
        }
        if ((x = userChangeInfoDto.getPassword()) != null) {
            user.setPassword(passwordEncoder.encode(x));
        }
        if ((x = userChangeInfoDto.getStatus()) != null) {
            user.setStatus(Status.valueOf(x));
        }
        if (userChangeInfoDto.getRoles() != null) {
            List<Role> roleList = new ArrayList<>();
            for (String roleName : userChangeInfoDto.getRoles()) { // Если роль не начинается с префикса ROLE_, то добавить этот префикс
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
        user = userRepository.save(user);
        return UserConverter.toUserMaxDto(user);

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
    public User findMe(String bearerToken) {
        String token = bearerToken.substring(7);
        Long myId = getMyId(token);
        return findById(myId);
    }

    @Override
    public void delete(User user) {
        deleteById(user.getId());
    }
}
