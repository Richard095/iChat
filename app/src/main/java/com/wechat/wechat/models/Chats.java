package com.wechat.wechat.models;

public class Chats {
    private String previewLastMessage;
    private String previewLastChatCreatedAt;
    private String contactUrlProfile;
    private String conversationId;
    private String userIdUno;
    private String userIdDOS;
    private String userNameUno;
    private String getUserNameDos;

    public Chats(){}

    public String getPreviewLastMessage() {
        return previewLastMessage;
    }

    public void setPreviewLastMessage(String previewLastMessage) {
        this.previewLastMessage = previewLastMessage;
    }

    public String getPreviewLastChatCreatedAt() {
        return previewLastChatCreatedAt;
    }

    public void setPreviewLastChatCreatedAt(String previewLastChatCreatedAt) {
        this.previewLastChatCreatedAt = previewLastChatCreatedAt;
    }

    public String getContactUrlProfile() {
        return contactUrlProfile;
    }

    public void setContactUrlProfile(String contactUrlProfile) {
        this.contactUrlProfile = contactUrlProfile;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUserIdUno() {
        return userIdUno;
    }

    public void setUserIdUno(String userIdUno) {
        this.userIdUno = userIdUno;
    }

    public String getUserIdDOS() {
        return userIdDOS;
    }

    public void setUserIdDOS(String userIdDOS) {
        this.userIdDOS = userIdDOS;
    }

    public String getUserNameUno() {
        return userNameUno;
    }

    public void setUserNameUno(String userNameUno) {
        this.userNameUno = userNameUno;
    }

    public String getGetUserNameDos() {
        return getUserNameDos;
    }

    public void setGetUserNameDos(String getUserNameDos) {
        this.getUserNameDos = getUserNameDos;
    }

    public Chats(String previewLastMessage, String previewLastChatCreatedAt, String contactUrlProfile, String conversationId, String userIdUno, String userIdDOS, String userNameUno, String getUserNameDos) {
        this.previewLastMessage = previewLastMessage;
        this.previewLastChatCreatedAt = previewLastChatCreatedAt;
        this.contactUrlProfile = contactUrlProfile;
        this.conversationId = conversationId;
        this.userIdUno = userIdUno;
        this.userIdDOS = userIdDOS;
        this.userNameUno = userNameUno;
        this.getUserNameDos = getUserNameDos;
    }
}
