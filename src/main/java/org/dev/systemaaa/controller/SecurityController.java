package org.dev.systemaaa.controller;

import lombok.RequiredArgsConstructor;
import org.dev.systemaaa.jwt.JwtCore;
import org.dev.systemaaa.model.dto.*;
import org.dev.systemaaa.model.entity.RefreshToken;
import org.dev.systemaaa.model.entity.User;
import org.dev.systemaaa.model.security.UserDetailsImpl;
import org.dev.systemaaa.service.RefreshTokenService;
import org.dev.systemaaa.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtCore jwtCore;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
        try {
            userService.registerUser(signupRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Регистрация успешна. Проверьте почту для подтверждения.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            userService.verifyEmail(token);
            return ResponseEntity.ok("Email подтверждён, можете войти");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signinRequest.getUsername(),
                            signinRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = jwtCore.generateToken(authentication);

            // Получаем User entity для создания refresh token
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userService.getUserById(userDetails.getId());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Неверный логин или пароль");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenService.verifyAndGet(request.getRefreshToken());
            String newAccessToken = jwtCore.generateTokenForUser(refreshToken.getUser());
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getToken()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        try {
            refreshTokenService.revokeToken(request.getRefreshToken());
        } catch (IllegalArgumentException ignored) {
            // Токен уже отозван или не существует — для клиента это всё равно успех
        }
        return ResponseEntity.noContent().build();
    }
}