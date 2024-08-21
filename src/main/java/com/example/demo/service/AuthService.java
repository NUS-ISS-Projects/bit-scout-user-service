package com.example.demo.service;

import com.example.demo.model.User;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AuthService {

    private final FirestoreService firestoreService;
    private final FirebaseAuth firebaseAuth;

    @Autowired
    public AuthService(FirestoreService firestoreService, FirebaseAuth firebaseAuth) {
        this.firestoreService = firestoreService;
        this.firebaseAuth = firebaseAuth;
    }

    public User createUser(User user) throws FirebaseAuthException, ExecutionException, InterruptedException {
        // Create a new user with Firebase Auth
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword());

        UserRecord userRecord = firebaseAuth.createUser(request);
        user.setUid(userRecord.getUid());
        firestoreService.addUser(user.getEmail(), user.getPassword());
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

    public String loginWithEmailPassword(String email, String password) throws FirebaseAuthException {
        try {
            // Authenticate with Firebase
            FirebaseToken firebaseToken = firebaseAuth.createCustomToken(email);
            return firebaseToken.getToken();
        } catch (FirebaseAuthException e) {
            throw new FirebaseAuthException(null, "Login failed", e, null, null);
        }
    }
}
