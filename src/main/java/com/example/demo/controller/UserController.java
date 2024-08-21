package com.example.demo.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.User;
import com.example.demo.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user)
            throws FirebaseAuthException, ExecutionException, InterruptedException {
        User createdUser = authService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.loginWithEmailPassword(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }
}
