package com.app.controllers;

import com.app.DTO.DTOChatSendMsgWithMillis;
import com.app.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/all")
    public List<DTOChatSendMsgWithMillis> getAllChatsByUser(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "50") Integer size,
                                                            Principal principal) {

        return chatService.getAllChatRoomsWithLastMsgByUserId(principal.getName(), page, size);
    }
}
