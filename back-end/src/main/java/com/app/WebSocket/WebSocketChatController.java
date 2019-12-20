package com.app.WebSocket;

import com.app.DTO.DTOChatInit;
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

        DTOChatInit dtoChatInitFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatInit.class);
        System.out.println(dtoChatInitFromJSON);

        simpMessagingTemplate.convertAndSend("/topic/chat", "FROM convert");

    }
}
