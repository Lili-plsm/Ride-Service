package ru.site.web.controller;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.site.domain.service.UserService;
import ru.site.security.model.JwtRequest;
import ru.site.security.model.JwtResponse;
import ru.site.security.model.RefreshJwtRequest;
import ru.site.security.service.AuthService;

@RestController
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid
                                      @RequestBody JwtRequest jwtRequest) {

        userService.register(jwtRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Пользователь успешно зарегистрирован"));
    }
    @PostMapping("/auth")
    public ResponseEntity<JwtResponse>
    auth(@Valid @RequestBody JwtRequest jwtRequest) {
        JwtResponse jwtResponse = authService.auth(jwtRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/update_access")
    public ResponseEntity<JwtResponse>
    updateAccessToken(@Valid @RequestBody RefreshJwtRequest refreshJwtRequest) {
        JwtResponse updatedJwt =
            authService.updateAccessToken(refreshJwtRequest.getRefreshToken());
        return ResponseEntity.ok(updatedJwt);
    }

    @PostMapping("/update_refresh")
    public ResponseEntity<JwtResponse> updateRefreshToken(
        @Valid @RequestBody RefreshJwtRequest refreshJwtRequest) {
        JwtResponse updatedJwt =
            authService.updateRefreshToken(refreshJwtRequest.getRefreshToken());
        return ResponseEntity.ok(updatedJwt);
    }
	
	@GetMapping("/users/current/id")
	public ResponseEntity<?> getCurrentUserId() {
		Long userId = userService.getCurrentUser().getId();
		return ResponseEntity.ok(Map.of("userId", userId));
	}
}
