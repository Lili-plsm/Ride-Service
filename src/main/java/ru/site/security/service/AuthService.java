package ru.site.security.service;

import ru.site.security.model.JwtAuthentication;
import ru.site.security.model.JwtRequest;
import ru.site.security.model.JwtResponse;

public interface AuthService {

  JwtResponse auth(JwtRequest jwtRequest);

  JwtResponse updateRefreshToken(String refreshToken);

  JwtResponse updateAccessToken(String refreshToken);

  JwtAuthentication getJwtAuthentication(String token);
}
