package com.app.services;

public interface ChatService {

    void chatInit(String dtoChatInit, String currentUserEmail);

    void sendMessage(String dtoChatMessage, String principalName);
}
