package ru.site.datasource.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.site.datasource.enums.RoleName;
import ru.site.datasource.model.Role;
import ru.site.datasource.model.User;
import ru.site.datasource.repository.RoleRepository;
import ru.site.datasource.repository.UserRepository;

@Service
public class UserRepositoryServiceImpl implements UserRepositoryService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRepositoryServiceImpl(UserRepository userRepository,
                                     RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User getUserByLogin(String login) {
        if (login == null) {
            throw new IllegalArgumentException("login не может быть null");
        }
        return userRepository.findByLogin(login).orElseThrow(
            () -> new EntityNotFoundException("User not found"));
    }

    public User getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                "id пользователя не может быть null");
        }
        return userRepository.findById(id).orElseThrow(
            () -> new EntityNotFoundException("User not found"));
    }

    public Long getUserIdByLogin(String login) {
        return getUserByLogin(login).getId();
    }

    public void saveUser(User user) {

        try {
            if (userRepository.findByLogin(user.getLogin()).isPresent()) {
                throw new IllegalStateException(
                    "User with this login already exists");
            }
            Role role =
                roleRepository.findByRoleName(RoleName.USER)
                    .orElseThrow(
                        () -> new EntityNotFoundException("No such role"));
            user.addRole(role);
            userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(
                "User with this login already exists", ex);
        }
    }
}