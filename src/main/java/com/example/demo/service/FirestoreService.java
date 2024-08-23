package com.example.demo.service;

import com.example.demo.model.User;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    public void addUser(String email, String password, String uid) throws ExecutionException, InterruptedException {
        CollectionReference users = firestore.collection("users");

        // Auto-generate ID
        DocumentReference docRef = users.document(uid); // This auto-generates the ID

        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setPassword(password);

        // Add the user document with the auto-generated ID
        docRef.set(user).get();
    }

    public User getUser(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("users").document(uid);
        return docRef.get().get().toObject(User.class);
    }
}
