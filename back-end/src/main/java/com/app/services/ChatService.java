package com.app.services;

import com.app.exceptions.CustomException;

public interface ChatService {

    void chatInit(String dtoChatInit, String currentUserEmail) throws CustomException;

    void sendMessage(String dtoChatMessage, String principalName) throws CustomException;

    void getMessagesByPage(String chatMessagesPagination) throws CustomException;
}
