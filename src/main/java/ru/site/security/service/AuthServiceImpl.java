package ru.site.security.service;

import io.jsonwebtoken.Claims;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import ru.site.datasource.model.User;
import ru.site.domain.service.UserService;
import ru.site.security.model.JwtAuthentication;
import ru.site.security.model.JwtRequest;
import ru.site.security.model.JwtResponse;
import ru.site.security.util.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

  private final JwtProvider jwtProvider;
  private final UserService userService;
  private final JwtUtil jwtUtil;

  public AuthServiceImpl(JwtProvider jwtProvider, UserService userService, JwtUtil jwtUtil) {
    this.jwtProvider = jwtProvider;
    this.userService = userService;
    this.jwtUtil = jwtUtil;
  }

  public JwtResponse auth(JwtRequest jwtRequest) {
    String login = jwtRequest.getLogin();
    String password = jwtRequest.getPassword();
    User user = userService.getUserByLogin(login);
    if (BCrypt.checkpw(password, user.getPassword())) {
      String accessToken = jwtProvider.genAccessToken(user);
      String refreshToken = jwtProvider.genRefreshToken(user);
      return new JwtResponse("Bearer", accessToken, refreshToken);
    }
    throw new RuntimeException("Invalid login or password");
  }

  public JwtResponse updateRefreshToken(String refreshToken) {
    if (jwtProvider.validateRefreshToken(refreshToken)) {
      Claims claims = jwtProvider.getClaimsFromToken(refreshToken);
      String login = claims.getSubject();
      User user = userService.getUserByLogin(login);
      String newAccessToken = jwtProvider.genAccessToken(user);
      String newRefreshToken = jwtProvider.genRefreshToken(user);
      return new JwtResponse("Bearer", newAccessToken, newRefreshToken);
    }
    throw new RuntimeException("Invalid refresh token");
  }

  public JwtResponse updateAccessToken(String refreshToken) {
    if (jwtProvider.validateRefreshToken(refreshToken)) {
      Claims claims = jwtProvider.getClaimsFromToken(refreshToken);
      String login = claims.getSubject();
      User user = userService.getUserByLogin(login);
      String newAccessToken = jwtProvider.genAccessToken(user);
      return new JwtResponse("Bearer", newAccessToken, null);
    }
    throw new RuntimeException("Invalid refresh token");
  }

  public JwtAuthentication getJwtAuthentication(String token) {
    Claims claims = jwtProvider.getClaimsFromToken(token);
    return jwtUtil.generate(claims);
  }
}
