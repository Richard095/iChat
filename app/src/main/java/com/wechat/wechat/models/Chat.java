package com.wechat.wechat.models;

public class Chat {

    private String username;
    private String message;
    private String created_At;
    private int profile;
    private String firstUserId;
    private String secondUserId;
    private String conversationId;


    public Chat(){}
    public Chat(String username, String message, String created_At, int profile, String firstUserId, String secondUserId, String conversationId) {
        this.username = username;
        this.message = message;
        this.created_At = created_At;
        this.profile = profile;
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.conversationId = conversationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_At() {
        return created_At;
    }

    public void setCreated_At(String created_At) {
        this.created_At = created_At;
    }

    public int getProfile() {
        return profile;
    }

    public void setProfile(int profile) {
        this.profile = profile;
    }

    public String getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(String firstUserId) {
        this.firstUserId = firstUserId;
    }

    public String getSecondUserId() {
        return secondUserId;
    }

    public void setSecondUserId(String secondUserId) {
        this.secondUserId = secondUserId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
