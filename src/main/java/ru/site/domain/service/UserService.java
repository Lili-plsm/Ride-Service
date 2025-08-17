package ru.site.domain.service;

import ru.site.datasource.model.User;
import ru.site.security.model.JwtRequest;

public interface UserService {
    public boolean register(JwtRequest jwtRequest);
    public String getCurrentLogin();
    public User getCurrentUser();
    public User getUserById(Long uuid);
    public User getUserByLogin(String login);
    public void saveUser(Long uuid);
    public Long getUserIdByLogin(String login);
}
