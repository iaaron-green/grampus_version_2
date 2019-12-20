package com.app.WebSocket;

import com.app.DTO.DTOChatMessage;
import com.app.services.ChatRoomService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketChatController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ChatRoomService chatRoomService;

    @MessageMapping({"/chat.chatInit"})
    public void chatInit(String dtoChatMessage, Principal principal) {
        chatRoomService.chatInit(dtoChatMessage, principal.getName());
    }

    @MessageMapping({"/chat.sendMessage"})
    public void sendMessage(String dtoChatMessage, Principal principal) {

        System.out.println("PRINCIPAL - "+ principal.getName());

        DTOChatMessage dtoChatMessageFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatMessage.class);
        System.out.println(dtoChatMessageFromJSON);

        simpMessagingTemplate.convertAndSend("/topic/chat", "FROM convert");

    }
}
