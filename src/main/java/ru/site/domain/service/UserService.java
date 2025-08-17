package ru.site.domain.service;

import ru.site.datasource.model.User;
import ru.site.security.model.JwtRequest;

public interface UserService {
  boolean register(JwtRequest jwtRequest);

  String getCurrentLogin();

  User getCurrentUser();

  User getUserById(Long uuid);

  User getUserByLogin(String login);

  void saveUser(Long uuid);

  Long getUserIdByLogin(String login);
}
