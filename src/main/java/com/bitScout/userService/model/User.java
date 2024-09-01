package com.bitScout.userService.model;

public class User {

    private String uid; // Unique ID used by Firebase Auth
    private String email;
    private String password;
    private String name;
    private String avatar; // Optional: URL to avatar image
    private String introduction; // Optional

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(String uid, String email, String password, String name, String avatar,
            String introduction) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.introduction = introduction;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", introduction='" + introduction + '\'' +
                '}';
    }
}