package com.app.controllers;

import com.app.entities.ChatMember;
import com.app.entities.ChatMessage;
import com.app.entities.Room;
import com.app.enums.ChatType;
import com.app.repository.ChatMemberRepository;
import com.app.repository.ChatMessageRepository;
import com.app.repository.RoomRepository;
import com.app.repository.UserRepository;
import com.app.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ChatController {

    UserRepository userRepository;
    RoomRepository roomRepository;
    ChatMessageRepository chatMessageRepository;
    ChatMemberRepository chatMemberRepository;
    RoomService roomService;

    @Autowired
    public ChatController(UserRepository userRepository, RoomRepository roomRepository, ChatMessageRepository chatMessageRepository,
                          ChatMemberRepository chatMemberRepository, RoomService roomService) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.roomService = roomService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic.public")
    public ChatMessage sendMessage(@PathVariable ChatMessage chatMessage) {
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMember addUser(@Payload ChatMember chatMember, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMember.getMemberId());
        return chatMember;
    }

    @GetMapping(path = "/chats/list/{userId}")
    public List<Room> getChatsByUser(@PathVariable Long userId) {
        List<Room> rooms = roomRepository.findAllByChatMemberId(userId);
        return rooms;
    }

    @PostMapping(path = "/chats/{roomId}/leave")
    public Boolean leaveChat(@PathVariable Long roomId, Long userId) {
        Room room = roomRepository.getById(roomId);
        ChatMember chatMember = chatMemberRepository.findByRoomId(userId);
        room.removeChatMember(chatMember);
        return true;
    }

    @PostMapping(path = "/chats/{roomId}/join")
    public Boolean joinChat(@PathVariable Long roomId, Long userId) {
        Room room = roomRepository.getById(roomId);
        ChatMember chatMember = chatMemberRepository.findByRoomId(userId);
        room.addChatMember(chatMember);
        return true;
    }

    @GetMapping(path = "/chat/{chatMemberId1}/{chatMemberId2}")
    public Room getOrCreateChat(@PathVariable Long chatMemberId1, @PathVariable Long chatMemberId2) {
        return roomService.getOrCreate(chatMemberId1, chatMemberId2);
    }

    @GetMapping(path = "/chat/{roomId}/chatMember")
    public List<ChatMember> getAllChatsMembers(@PathVariable Long roomId) {
        return chatMemberRepository.findAllByRoomId(roomId);
    }

    @GetMapping(path = "/chats/public")
    public List<Room> getChatsByUser(@PathVariable ChatType type) {
        List<Room> rooms = roomRepository.findByChatType(type);
        return rooms;

    }
}
