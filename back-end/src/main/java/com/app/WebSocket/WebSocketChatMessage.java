package com.app.WebSocket;

public class WebSocketChatMessage {
    private String type;
    private String content;
    private String sender;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "WebSocketChatMessage{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}
