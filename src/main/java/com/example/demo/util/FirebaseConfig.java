package com.example.demo.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static boolean firebaseInitialized = false;

    @Bean
    public Firestore firestore() throws IOException {
        if (!firebaseInitialized) {
            // Initialize FirebaseApp only if it hasn't been initialized before
            initializeFirebaseApp();
            firebaseInitialized = true;
        }
        return FirestoreClient.getFirestore();
    }

    private void initializeFirebaseApp() throws IOException {
        // Get the current working directory
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("PWD:" + currentDirectory);

        // // Construct the file path relative to the current working directory
        // String filePath = currentDirectory + File.separator + "app" + File.separator
        // + "google-services.json";
        String filePath = "app/google-services.json";
        try {
            System.out.println("Attempting to load file from: " + filePath);
            FileInputStream serviceAccount = new FileInputStream(filePath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase application initialized");
        } catch (FileNotFoundException error) {
            // Handle file not found error
            System.err.println("File not found: " + error.getMessage());
        } catch (IOException e) {
            // Handle IO exception
            System.err.println("IOException: " + e.getMessage());
            // Handle other IO exceptions
            e.printStackTrace();
        }
    }
}