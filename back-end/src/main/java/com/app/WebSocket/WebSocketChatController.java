package com.app.WebSocket;

import com.app.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
public class WebSocketChatController {


    private ChatService chatService;

    @Autowired
    public WebSocketChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @MessageMapping({"/chat.chatInit"})
    public void chatInit(String dtoChatMessage, Principal principal) {
        chatService.chatInit(dtoChatMessage, principal.getName());
    }

    @MessageMapping({"/chat.sendMessage"})
    public void sendMessage(String dtoChatMessage, Principal principal) {
        chatService.sendMessage(dtoChatMessage, principal.getName());
    }
}
