package com.bitScout.userService.controller;

import java.util.concurrent.ExecutionException;

import com.bitScout.userService.dto.UpdateEmailPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bitScout.userService.dto.LoginRequest;
import com.bitScout.userService.dto.UpdateUserRequest;
import com.bitScout.userService.model.User;
import com.bitScout.userService.service.AuthService;
import com.bitScout.userService.service.FirestoreService;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuthException;

@RestController
@RequestMapping("/account")
public class UserController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FirestoreService firestoreService;

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

    @GetMapping("/user")
    public ResponseEntity<?> getUserByToken(@RequestParam("token") String token) {
        try {
            User user = authService.getUserByToken(token);
            return ResponseEntity.ok(user);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/updateUserDetails")
    public ResponseEntity<?> updateUserDetails(@PathVariable String userId,
            @RequestBody UpdateUserRequest updateUserRequest) {
        try {
            // Update user details in Firestore
            firestoreService.updateUser(userId,
                    updateUserRequest.getName(),
                    updateUserRequest.getAvatar(),
                    updateUserRequest.getIntroduction());

            return ResponseEntity.ok("User details updated successfully.");
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    @PutMapping("/{userId}/updateUserEmailPassword")
    public ResponseEntity<?> updateUserEmailPassword(@PathVariable String userId,
            @RequestBody UpdateEmailPasswordRequest updateUserDetailsRequest) {
        try {
            // Authenticate the old password
            if (updateUserDetailsRequest.getNewPassword() != null
                    || !updateUserDetailsRequest.getNewPassword().isEmpty()
                    ||  updateUserDetailsRequest.getOldPassword() !=null
                    ||  !updateUserDetailsRequest.getOldPassword().isEmpty()) {
                boolean isAuthenticated = authService.authenticateUser(userId,
                        updateUserDetailsRequest.getOldPassword());

                if (!isAuthenticated) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid old password.");
                }
            }

            // Update user details (email and/or password) in Auth
            authService.updateUserEmailPassword(userId, updateUserDetailsRequest.getNewEmail(),
                    updateUserDetailsRequest.getNewPassword());

            // Update user details in Firestore
            firestoreService.updateUserEmailPassword(userId, updateUserDetailsRequest.getNewEmail(),
                    updateUserDetailsRequest.getNewPassword());

            return ResponseEntity.ok("User details updated successfully.");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user details: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }
}
