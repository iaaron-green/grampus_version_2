package com.app.WebSocket;

import com.app.exceptions.CustomException;
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
    public void chatInit(String dtoChatMessage, Principal principal) throws CustomException {
      chatService.chatInit(dtoChatMessage, principal.getName());
    }

    @MessageMapping({"/chat.getMessages"})
    public void chatInit(String chatMessagesPagination) throws CustomException {
        chatService.getMessagesByPage(chatMessagesPagination);
    }

    @MessageMapping({"/chat.sendMessage"})
    public void sendMessage(String dtoChatMessage, Principal principal) throws CustomException {
        chatService.sendMessage(dtoChatMessage, principal.getName());
    }
}
