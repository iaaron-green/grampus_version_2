package com.app.services.impl;

import com.app.DTO.DTOChatData;
import com.app.DTO.DTOChatInit;
import com.app.DTO.DTOChatSendMessage;
import com.app.DTO.DTOChatReceivedMessage;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

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
    public void chatInit(String dto, String currentUserEmail) {

        if (StringUtils.isEmpty(dto)) {
            System.out.println("throw exception wrong input data ");
        }

        User currentUser = userRepository.findByEmail(currentUserEmail);

        DTOChatInit dtoChatInit = new Gson().fromJson(dto, DTOChatInit.class);

        if (dtoChatInit.getTargetUserId() == null || StringUtils.isEmpty(dtoChatInit.getChatType())) {
            System.out.println("throw exception wrong input data ");
        }

        Long currentRoomId = roomRepository.getRoomIdByMembersIdAndChatType(currentUser.getId(),
                dtoChatInit.getTargetUserId(), dtoChatInit.getChatType().toString());

        String roomURL;
        Page<DTOChatSendMessage> chatMessages = new PageImpl<>(new ArrayList<>());

        if (currentRoomId == null) {

            Room newRoom = new Room();
            newRoom.setChatType(dtoChatInit.getChatType());
            newRoom = roomRepository.save(newRoom);

            ChatMember currentChatMember = new ChatMember();
            currentChatMember.setMemberId(currentUser.getId());
            currentChatMember.setRoom(newRoom);
            ChatMember targetChatMember = new ChatMember();
            targetChatMember.setMemberId(dtoChatInit.getTargetUserId());
            targetChatMember.setRoom(newRoom);
            chatMemberRepository.save(currentChatMember);
            chatMemberRepository.save(targetChatMember);

            roomURL = "/topic/chat" + newRoom.getId();

        } else {
            roomURL = "/topic/chat" + currentRoomId;
            chatMessages = chatMessageRepository.getMessagesByRoomId(currentRoomId, pageRequest(dtoChatInit.getPage(), dtoChatInit.getSize()));
        }

        simpMessagingTemplate.convertAndSend("/topic/chatListener",
                new Gson().toJson(new DTOChatData(currentUser.getId(), dtoChatInit.getTargetUserId(), roomURL, chatMessages.getContent())));
    }


    @Override
    public void sendMessage(String dtoChatMessage, String principalName) {

        User loggedUser = userRepository.findByEmail(principalName);
        if (loggedUser == null) {
            //throw new CustomException();
        }

        DTOChatReceivedMessage dtoChatReceivedMessageFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatReceivedMessage.class);

        Room chatRoom = roomRepository.getById(dtoChatReceivedMessageFromJSON.getRoomId());
        if (chatRoom != null){

            ChatMessage message = new ChatMessage(loggedUser.getId(), dtoChatReceivedMessageFromJSON.getTextMessage(), chatRoom);
            message = chatMessageRepository.save(message);

            DTOChatSendMessage dtoChatSendMessage = new DTOChatSendMessage(loggedUser.getId(), loggedUser.getProfile().getProfilePicture(),
                    loggedUser.getFullName(), message.getCreateDate(), message.getMessage());

            simpMessagingTemplate.convertAndSend("/topic/chat" + chatRoom.getId(), new Gson().toJson(dtoChatSendMessage));
        } else {
            //throw new CustomException();
        }
    }

    private Pageable pageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }
}
