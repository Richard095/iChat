package com.wechat.wechat.models;

public class User {
    private String username ;
    private String email ;
    private String userId;
    private String urlProfile;
    private String fullname;
    private String password;
    private String description;

    public User(){}

    public User(String username, String email, String userId, String urlProfile, String fullname, String password, String description) {
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.urlProfile = urlProfile;
        this.fullname = fullname;
        this.password = password;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
