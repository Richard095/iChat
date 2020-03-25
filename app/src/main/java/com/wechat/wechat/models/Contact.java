package com.wechat.wechat.models;

public class Contact {
    private String username;
    private String fullname;
    private String email;
    private String userId;
    private int imageId;
    private String status;
    public Contact(String username, String fullname, String email, String userId, int imageId, String status) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.userId = userId;
        this.imageId = imageId;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
}
