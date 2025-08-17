package ru.site.security.util;

import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ru.site.security.model.JwtAuthentication;

@Component
public class JwtUtil {

  public JwtAuthentication generate(Claims claims) {

    String login = claims.getSubject();

    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) claims.get("roles", List.class);
    Collection<? extends GrantedAuthority> authorities = Collections.emptyList();

    if (roles != null) {
      authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    return new JwtAuthentication(login, authorities);
  }
}
