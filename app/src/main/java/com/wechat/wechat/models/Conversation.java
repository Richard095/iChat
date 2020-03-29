package com.wechat.wechat.models;

public class Conversation {
    private String message;
    private String created_At;
    private String urlImage;
    private String messageType; //TEXT || IMAGE
    private String conversationId;

    public Conversation(){}

    public Conversation(String message, String created_At, String urlImage, String messageType, String conversationId) {
        this.message = message;
        this.created_At = created_At;
        this.urlImage = urlImage;
        this.messageType = messageType;
        this.conversationId = conversationId;
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

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
