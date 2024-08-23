package com.example.demo.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/account")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user)
            throws FirebaseAuthException, ExecutionException, InterruptedException {
        try {
            // Create user
            User createdUser = authService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.EMAIL_ALREADY_EXISTS) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("User creation failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {

            String token = authService.loginWithEmailPassword(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            if (e.getMessage().contains("INVALID_LOGIN_CREDENTIALS")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: Invalid email or password.");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @GetMapping("/userId")
    public ResponseEntity<?> getUserId(@RequestParam("token") String token) {
        try {
            String userId = authService.getUserIdFromToken(token);
            return ResponseEntity.ok(userId);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        }
    }
}
