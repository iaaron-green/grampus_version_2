package com.app.controllers;

import com.app.entities.ChatRoom;
import com.app.entities.User;
import com.app.repository.UserRepository;
import com.app.entities.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    UserRepository userRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic.public")
    public ChatMessage sendMessage(@PathVariable ChatMessage chatMessage){
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
//    @MessageMapping("/chat.addChat")
//    @PostMapping(path = "/chat/addChats")
//    public ChatRoom createPrivateChat(@RequestBody String postPayload) {
//        return ;
//    }

//    @GetMapping(path = "/api/private-chats/list/{userId}")
//    public List<ChatRoom> getChatsByUser(@PathVariable Long userId) {
//        ArrayList<ChatRoom> rooms = chatRoomRepository.getByUserId(userId);
//        rooms.removeIf(c -> !c.isPrivate());
//        return rooms;
//    }
//    @PostMapping(path = "/chats/{roomId}/leave")
//    public Boolean leaveChat(@PathVariable int roomId, Long userId) {
//        ChatRoom room = (ChatRoom) chatRoomRepository.getById(roomId);
//        User user = userRepository.getById(userId);
//        room.userRoom(user);
//        return (chatRoomRepository.save(room) > 0);
//    }
}
