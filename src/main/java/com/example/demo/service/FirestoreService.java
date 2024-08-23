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

    public void addUser(String uid, String email, String password, String username, String name, String avatar,
            String introduction) throws ExecutionException, InterruptedException {
        CollectionReference users = firestore.collection("users");

        // Use the UID as the document ID
        DocumentReference docRef = users.document(uid);

        // Create a new User object with all fields
        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        user.setName(name);
        user.setAvatar(avatar);
        user.setIntroduction(introduction);

        // Add the user document with the UID as the document ID
        docRef.set(user).get();
    }

    public User getUser(String uid) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("users").document(uid);
        return docRef.get().get().toObject(User.class);
    }
}
