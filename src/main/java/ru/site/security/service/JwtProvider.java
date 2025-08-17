package ru.site.security.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import ru.site.JwtSettings;
import ru.site.datasource.model.User;

@Service
public class JwtProvider {

  private final SecretKey key;

  private final long expirationTimeAccess;
  private final long expirationTimeRefresh;

  public JwtProvider(JwtSettings jwtSettings) {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSettings.getSecretKey()));
    this.expirationTimeAccess = jwtSettings.getExpirationTimeAccess();
    this.expirationTimeRefresh = jwtSettings.getExpirationTimeRefresh();
  }

  public String genAccessToken(User user) {
    return Jwts.builder()
        .setSubject(user.getLogin())
        .claim("id", Long.toString(user.getId()))
        .claim("roles", user.getRoles().stream().map(role -> role.getRoleName()).toList())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationTimeAccess))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public String genRefreshToken(User user) {
    return Jwts.builder()
        .setSubject(user.getLogin())
        .claim("id", Long.toString(user.getId()))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationTimeRefresh))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  private Claims parseClaims(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (JwtException | IllegalArgumentException e) {
      return null;
    }
  }

  public boolean validateAccessToken(String token) {
    return parseClaims(token) != null;
  }

  public boolean validateRefreshToken(String token) {
    return parseClaims(token) != null;
  }

  public Claims getClaimsFromToken(String token) {
    return parseClaims(token);
  }
}
