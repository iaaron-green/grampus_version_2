package com.app.WebSocket;

import com.app.DTO.DTOChatMember;
import com.app.DTO.DTOChatMessage;
import com.google.gson.Gson;
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

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping({"/chat.startChat"})
    @SendTo("/topic/chatsListener")
    public WebSocketChatMessage startChat(@Payload WebSocketChatMessage webSocketChatMessage) {
        return webSocketChatMessage;
    }

    @MessageMapping({"/chat.sendMessage"})
    public void sendMessage(String dtoChatMessage, Principal principal) {

        System.out.println("PRINCIPAL - "+ principal.getName());

        DTOChatMessage dtoChatMessageFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatMessage.class);

        simpMessagingTemplate.convertAndSend("/topic/chat/" + dtoChatMessageFromJSON.getRoomId(), dtoChatMessageFromJSON.getTextMessage());
    }
}
