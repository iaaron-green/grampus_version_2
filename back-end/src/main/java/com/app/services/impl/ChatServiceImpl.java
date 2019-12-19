package com.app.services.impl;

import com.app.DTO.DTOChatMessage;
import com.app.entities.ChatMessage;
import com.app.entities.Room;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.repository.ChatMessageRepository;
import com.app.repository.RoomRepository;
import com.app.repository.UserRepository;
import com.app.services.ChatService;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {
    private Logger logger = LogManager.getLogger(ChatServiceImpl.class);

    private SimpMessagingTemplate simpMessagingTemplate;
    private ChatMessageRepository chatMessageRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;

    @Autowired
    public ChatServiceImpl(SimpMessagingTemplate simpMessagingTemplate, ChatMessageRepository chatMessageRepository,
                           RoomRepository roomRepository, UserRepository userRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void sendMessage(String dtoChatMessage, String principalName) {

        User loggedUser = userRepository.findByEmail(principalName);
        if (loggedUser == null) {
            //throw new CustomException();
        }

        String someString = "\"WTF\":\"WTF\"";
        DTOChatMessage dtoChatMessageFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatMessage.class);

        Room chatRoom = roomRepository.getById(dtoChatMessageFromJSON.getRoomId());
        if (chatRoom != null){

            ChatMessage message = new ChatMessage(loggedUser.getId(), dtoChatMessageFromJSON.getTextMessage(), chatRoom);
            chatMessageRepository.save(message);
            simpMessagingTemplate.convertAndSend("/topic/chat/" + chatRoom.getId(), message.getMessage());
        } else {
            //throw new CustomException();
        }
    }
}
