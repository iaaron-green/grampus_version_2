package com.app.WebSocket;

import com.app.DTO.DTOChatMember;
import com.app.DTO.DTOChatMessage;
import com.app.entities.User;
import com.app.repository.ChatMessageRepository;
import com.app.repository.RoomRepository;
import com.app.repository.UserRepository;
import com.app.services.ChatService;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
public class WebSocketChatController {


    private ChatService chatService;

    @Autowired
    public WebSocketChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @MessageMapping({"/chat.startChat"})
    @SendTo("/topic/chatsListener")
    public WebSocketChatMessage startChat(@Payload WebSocketChatMessage webSocketChatMessage) {
        return webSocketChatMessage;
    }

    @MessageMapping({"/chat.sendMessage"})
    public void sendMessage(String dtoChatMessage, Principal principal) {

     chatService.sendMessage(dtoChatMessage, principal.getName());
    }
}
