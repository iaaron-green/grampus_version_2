package com.app.services;

import com.app.DTO.DTOChatSendMsgWithMillis;
import com.app.exceptions.CustomException;

import java.util.List;

public interface ChatService {

    void chatInit(String dtoChatInit, String currentUserEmail) throws CustomException;

    void sendMessage(String dtoChatMessage, String principalName) throws CustomException;

    void getMessagesByPage(String chatMessagesPagination) throws CustomException;

    List<DTOChatSendMsgWithMillis> getAllChatRoomsWithLastMsgByUserId(String email, Integer page, Integer size);
}
