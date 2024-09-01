package com.bitScout.userService.service;

import com.bitScout.userService.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Import BCrypt library for password hashing
import org.mindrot.jbcrypt.BCrypt;

import java.util.concurrent.ExecutionException;

@Service
public class AuthService {

    private final FirestoreService firestoreService;
    private final FirebaseAuth firebaseAuth;
    private static final String FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    @Value("${firebase.api.key}")
    private String firebaseApiKey;

    @Autowired
    public AuthService(FirestoreService firestoreService, FirebaseAuth firebaseAuth) {
        this.firestoreService = firestoreService;
        this.firebaseAuth = firebaseAuth;
    }

    public User createUser(User user) throws FirebaseAuthException, ExecutionException, InterruptedException {
         String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        // Create a new user with Firebase Auth
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());

        // Create the user in Firebase Auth
        UserRecord userRecord = firebaseAuth.createUser(request);

        // Set the UID from Firebase Auth
        user.setUid(userRecord.getUid());

        // Add the user to Firestore with all the fields
        firestoreService.addUser(user.getUid(), user.getEmail(),hashedPassword,
                user.getName(), null, null);

        // Return the created user
        return user;
    }

    public User getUserByToken(String idToken) throws FirebaseAuthException, ExecutionException, InterruptedException {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            User user = firestoreService.getUser(uid);

            if (user == null) {
                throw new NullPointerException("User not found for the given token.");
            }

            return user;
        } catch (FirebaseAuthException e) {
            throw e;
        }
    }

    public String loginWithEmailPassword(String email, String password) throws Exception {
         User user = firestoreService.getUserByEmail(email);

         if (user == null) {
         throw new Exception("User not found");
         }

         if (!BCrypt.checkpw(password, user.getPassword())) {
         throw new Exception("Invalid Password");
         }

        // Create a RestTemplate to send the HTTP request
        RestTemplate restTemplate = new RestTemplate();
//        System.out.println("Email: " + email);
//        System.out.println("Password: " + password); // Do not log passwords in production!

        // Prepare the request payload
        String payload = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email,
                password);

        // Set the headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // Create the request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(payload, headers);

        // Build the Firebase Auth URL
        String url = FIREBASE_AUTH_URL + firebaseApiKey;

        // Send the request
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse the response to get the ID token
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response.getBody());
            String idToken = root.path("idToken").asText();
            return idToken;
        } else {
            // Handle error cases
            throw new Exception("Failed to log in with email and password");
        }
    }

    public String getUserIdFromToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
        return decodedToken.getUid();
    }

    public void updateUserEmailPassword(String uid, String newEmail, String newPassword) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid);

        // Update email if provided
        if (newEmail != null && !newEmail.isEmpty()) {
            request.setEmail(newEmail);
        }

        // Update password if provided
        if (newPassword != null && !newPassword.isEmpty()) {
            request.setPassword(newPassword);
        }

        firebaseAuth.updateUser(request);
    }

    public String getEmailByUid(String uid) throws Exception {
        try {
            // Retrieve user record from UID
            UserRecord userRecord = firebaseAuth.getUser(uid);
            // Return the user's email
            return userRecord.getEmail();
        } catch (FirebaseAuthException e) {
            throw new Exception("Get user email");
        }
    }

    public boolean authenticateUser(String id, String oldPassword) throws FirebaseAuthException {
        try {
            User user = firestoreService.getUserByEmail(getEmailByUid(id));

            if (user == null) {
                throw new Exception("User not found");
            }

            if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
                throw new Exception("Invalid Password");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }
}
