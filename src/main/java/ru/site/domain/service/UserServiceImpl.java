package ru.site.domain.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.User;
import ru.site.datasource.service.UserRepositoryService;
import ru.site.security.model.JwtRequest;
import ru.site.security.util.PasswordUtils;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepositoryService userRepositoryService;

  public UserServiceImpl(UserRepositoryService userRepositoryService) {
    this.userRepositoryService = userRepositoryService;
  }

  public boolean register(JwtRequest jwtRequest) {
    String login = jwtRequest.getLogin();
    String password = PasswordUtils.hashPassword(jwtRequest.getPassword());

    if (login == null || password == null)
      throw new IllegalArgumentException("Login или password не могут быть null");

    User newUser = new User();
    newUser.setLogin(login);
    newUser.setPassword(password);
    userRepositoryService.saveUser(newUser);
    return true;
  }

  public String getCurrentLogin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (String) auth.getPrincipal();
  }

  public User getCurrentUser() {
    return userRepositoryService.getUserByLogin(getCurrentLogin());
  }

  public User getUserById(Long id) {
    return userRepositoryService.getUserById(id);
  }

  public User getUserByLogin(String login) {
    Long id = getUserIdByLogin(login);
    return userRepositoryService.getUserById(id);
  }

  public Long getUserIdByLogin(String login) {
    return userRepositoryService.getUserIdByLogin(login);
  }

  public void saveUser(Long uuid) {
    User user = getUserById(uuid);
    userRepositoryService.saveUser(user);
  }
}
