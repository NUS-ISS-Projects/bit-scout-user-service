package com.bitScout.userService.service;

import com.bitScout.userService.model.User;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

//Cindy added here for getting the user validity
import com.google.cloud.firestore.QueryDocumentSnapshot;
import java.util.List;
//
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FirestoreService {

    @Autowired
    private Firestore firestore;

    public void addUser(String uid, String email, String password, String name, String avatar,
            String introduction) throws ExecutionException, InterruptedException {
        CollectionReference users = firestore.collection("users");

        // Use the UID as the document ID
        DocumentReference docRef = users.document(uid);

        // Create a new User object with all fields
        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setPassword(password);
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

    public User getUserByEmail(String email) throws ExecutionException, InterruptedException {
        // Query Firestore for the user document
        QuerySnapshot querySnapshot = firestore.collection("users").whereEqualTo("email", email).get().get();
        List<QueryDocumentSnapshot> document = querySnapshot.getDocuments();
        if (!document.isEmpty()) {
            return document.get(0).toObject(User.class);
        }
        return null;
    }

    // Method to update user details
    public void updateUser(String uid, String name, String avatar, String introduction)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("users").document(uid);
        Map<String, Object> updates = new HashMap<>();
        if (name != null)
            updates.put("name", name);
        if (avatar != null)
            updates.put("avatar", avatar);
        if (introduction != null)
            updates.put("introduction", introduction);
        docRef.update(updates).get();
    }
    public void updateUserEmailPassword(String uid, String newEmail, String newPassword)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("users").document(uid);
        Map<String, Object> updates = new HashMap<>();
        if (newEmail != null && !newEmail.isEmpty())
            updates.put("email", newEmail);
        if(newPassword != null && !newPassword.isEmpty())
            updates.put("password", BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        docRef.update(updates).get();
    }
}
