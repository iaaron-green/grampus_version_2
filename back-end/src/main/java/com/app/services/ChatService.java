package com.app.services;

import com.app.DTO.DTOChatInit;

public interface ChatService {

    void chatInit(DTOChatInit dtoChatMessage, String currentUserEmail);

    void chatInitTest(String dtoChatInit, String currentUserEmail);

    void sendMessage(String dtoChatMessage, String principalName);
}
