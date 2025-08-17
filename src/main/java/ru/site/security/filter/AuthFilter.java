package ru.site.security.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.site.security.service.AuthService;
import ru.site.security.service.JwtProvider;

@Component
public class AuthFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final AuthService authService;

  public AuthFilter(JwtProvider jwtProvider, AuthService authService) {
    this.jwtProvider = jwtProvider;
    this.authService = authService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      if (jwtProvider.validateAccessToken(token)) {
        Claims claims = jwtProvider.getClaimsFromToken(token);
        if (claims != null) {
          Authentication auth = authService.getJwtAuthentication(token);
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      }
    }

    filterChain.doFilter(request, response);
  }
}
