package com.wechat.wechat.models;

public class Chats {
    private String previewLastMessage;
    private String previewLastChatCreatedAt;
    private String contactUrlProfile;
    private String conversationId;

    public Chats(){}

    public Chats(String previewLastMessage, String previewLastChatCreatedAt, String contactUrlProfile, String conversationId) {
        this.previewLastMessage = previewLastMessage;
        this.previewLastChatCreatedAt = previewLastChatCreatedAt;
        this.contactUrlProfile = contactUrlProfile;
        this.conversationId = conversationId;
    }

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
}
