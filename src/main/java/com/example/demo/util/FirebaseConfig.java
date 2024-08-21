package com.example.demo.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static boolean firebaseInitialized = false;

    @Bean
    public Firestore firestore() throws IOException {
        initializeFirebaseApp(); // Ensure Firebase is initialized before accessing Firestore
        return FirestoreClient.getFirestore();
    }

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        initializeFirebaseApp(); // Ensure Firebase is initialized before accessing FirebaseAuth
        return FirebaseAuth.getInstance();
    }

    private void initializeFirebaseApp() throws IOException {
        if (!firebaseInitialized) {
            String filePath = "app/google-services.json";
            try {
                FileInputStream serviceAccount = new FileInputStream(filePath);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
                firebaseInitialized = true;
                System.out.println("Firebase application initialized");
            } catch (IOException e) {
                System.err.println("IOException: " + e.getMessage());
                e.printStackTrace();
                throw e; // Re-throw to indicate failure in initializing
            }
        }
    }
}
