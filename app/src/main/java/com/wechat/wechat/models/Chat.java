package com.wechat.wechat.models;

public class Chat {

    private String username;
    private String message;
    private String created_At;
    private String urlProfile;
    private String secondUserId;
    private String conversationId;
    private int countNewMessages;
    private String firstUserId;
    public Chat(){}

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

    public String getUrlProfile() {
        return urlProfile;
    }

    public void setUrlProfile(String urlProfile) {
        this.urlProfile = urlProfile;
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

    public int getCountNewMessages() {
        return countNewMessages;
    }

    public void setCountNewMessages(int countNewMessages) {
        this.countNewMessages = countNewMessages;
    }

    public String getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(String firstUserId) {
        this.firstUserId = firstUserId;
    }

    public Chat(String username, String message, String created_At, String urlProfile, String secondUserId, String conversationId, int countNewMessages, String firstUserId) {
        this.username = username;
        this.message = message;
        this.created_At = created_At;
        this.urlProfile = urlProfile;
        this.secondUserId = secondUserId;
        this.conversationId = conversationId;
        this.countNewMessages = countNewMessages;
        this.firstUserId = firstUserId;
    }
}
