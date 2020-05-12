package com.wechat.wechat.models;

public class Invitation {
    private String username;
    private String userId;
    private String conversationId;


    public Invitation(){}

    public Invitation(String username, String userId, String conversationId) {
        this.username = username;
        this.userId = userId;
        this.conversationId = conversationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
