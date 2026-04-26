package org.dev.systemaaa.controller;

import lombok.RequiredArgsConstructor;
import org.dev.systemaaa.jwt.JwtCore;
import org.dev.systemaaa.model.dto.SigninRequest;
import org.dev.systemaaa.model.dto.SignupRequest;
import org.dev.systemaaa.model.entity.User;
import org.dev.systemaaa.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class SecurtiryController {
    private  final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtCore jwtCore;
    private User user;

    @PostMapping("/singup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest signupRequest) {
         if (userRepository.existsByusername((SignupRequest.getUsername())){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choose different username");
         }
         if (userRepository.existsByEmail(((SignupRequest.getEmail())){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists change different username or email");
         }
         user.setUsername(signupRequest.getUsername()));
         user.setEmail(signupRequest.getEmail());
         user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
         userRepository.save(user);
         return ResponseEntity.status(HttpStatus.OK).body("User registered successfully");
    }
    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody SigninRequest signinRequest) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword()));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtCore.generateToken(authentication);
        return ResponseEntity.status(HttpStatus.OK).body("User logged in successfully");
    }
}
