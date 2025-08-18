package ru.site.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.site.security.filter.AuthFilter;
import ru.site.security.service.AuthService;
import ru.site.security.service.JwtProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public AuthFilter authFilter(JwtProvider jwtProvider, AuthService authService) {
    return new AuthFilter(jwtProvider, authService);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthFilter authFilter)
      throws Exception {
    return http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/register",
                        "/auth",
                        "/update_access",
                        "/update_refresh")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }
}
