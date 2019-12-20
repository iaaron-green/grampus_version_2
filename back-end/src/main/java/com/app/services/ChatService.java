package com.app.services;

import com.app.DTO.DTOChatInit;

public interface ChatService {

    void chatInit(DTOChatInit dtoChatMessage, String currentUserEmail);

    void sendMessage(String dtoChatMessage, String principalName);
}
