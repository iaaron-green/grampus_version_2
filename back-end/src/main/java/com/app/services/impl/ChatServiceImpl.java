package com.app.services.impl;

import com.app.DTO.DTOChatInit;
import com.app.DTO.DTOChatMessage;
import com.app.entities.ChatMember;
import com.app.entities.ChatMessage;
import com.app.entities.Room;
import com.app.entities.User;
import com.app.repository.ChatMemberRepository;
import com.app.repository.ChatMessageRepository;
import com.app.repository.RoomRepository;
import com.app.repository.UserRepository;
import com.app.services.ChatService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ChatServiceImpl implements ChatService {

    private SimpMessagingTemplate simpMessagingTemplate;
    private ChatMessageRepository chatMessageRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private ChatMemberRepository chatMemberRepository;

    @Autowired
    public ChatServiceImpl(SimpMessagingTemplate simpMessagingTemplate, ChatMessageRepository chatMessageRepository,
                           RoomRepository roomRepository, UserRepository userRepository, ChatMemberRepository chatMemberRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.chatMemberRepository = chatMemberRepository;
    }

    @Override
    public void chatInit(DTOChatInit dtoChatInit, String currentUserEmail) {

        if (StringUtils.isEmpty(dtoChatInit)) {
            System.out.println("throw exception wrong input data ");
        }

        User currentUser = userRepository.findByEmail(currentUserEmail);

        if (dtoChatInit.getTargetUserId() == null || StringUtils.isEmpty(dtoChatInit.getChatType())) {
            System.out.println("throw exception wrong input data ");
        }

        Long currentRoomId = roomRepository.getRoomIdByMembersIdAndChatType(currentUser.getId(),
                dtoChatInit.getTargetUserId(), dtoChatInit.getChatType());

        String roomDestination;
        if (currentRoomId != null)
            roomDestination = "/topic/chat/" + currentRoomId;
        else {
            ChatMember currentChatMember = new ChatMember();
            currentChatMember.setMemberId(currentUser.getId());
            ChatMember targetChatMember = new ChatMember();
            targetChatMember.setMemberId(dtoChatInit.getTargetUserId());
            Room newRoom = new Room();
            newRoom.setChatType(dtoChatInit.getChatType());
            newRoom = roomRepository.save(newRoom);
            currentChatMember.setRoom(newRoom);
            targetChatMember.setRoom(newRoom);
            chatMemberRepository.save(currentChatMember);
            chatMemberRepository.save(targetChatMember);

            roomDestination = "/topic/chat/" + newRoom.getId();
        }

        simpMessagingTemplate.convertAndSend("/topic/chatListener", roomDestination);
    }

    @Override
    public void sendMessage(String dtoChatMessage, String principalName) {

        User loggedUser = userRepository.findByEmail(principalName);
        if (loggedUser == null) {
            //throw new CustomException();
        }

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
