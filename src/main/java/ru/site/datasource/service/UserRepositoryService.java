package ru.site.datasource.service;

import org.springframework.stereotype.Service;
import ru.site.datasource.model.User;

@Service
public interface UserRepositoryService {

  User getUserById(Long uuid);

  Long getUserIdByLogin(String login);

  User getUserByLogin(String login);

  void saveUser(User user);
}
