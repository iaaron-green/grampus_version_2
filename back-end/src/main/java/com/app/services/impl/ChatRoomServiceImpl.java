package com.app.services.impl;

import com.app.DTO.DTOChatMessage;
import com.app.entities.ChatMember;
import com.app.entities.Room;
import com.app.entities.User;
import com.app.enums.ChatType;
import com.app.repository.ChatMemberRepository;
import com.app.repository.ChatRoomRepository;
import com.app.repository.UserRepository;
import com.app.services.ChatRoomService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ChatMemberRepository chatMemberRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void chatInit(@ModelAttribute String dtoChatMessage, String currentUserEmail) {

        if (StringUtils.isEmpty(dtoChatMessage)) {
            System.out.println("throw exception wrong input data ");
        }

        DTOChatMessage dtoChatMessageFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatMessage.class);
        User currentUser = userRepository.findByEmail(currentUserEmail);

        if (dtoChatMessageFromJSON.getDestinationUserId() == null || StringUtils.isEmpty(dtoChatMessageFromJSON.getChatType())) {
            System.out.println("throw exception wrong input data ");
        }

        Long currentRoomId = chatRoomRepository.getRoomIdByMembersIdAndChatType(currentUser.getId(),
                dtoChatMessageFromJSON.getDestinationUserId(), dtoChatMessageFromJSON.getChatType());

        String roomDestination;
        if (currentRoomId != null)
            roomDestination = "/topic/chat/" + currentRoomId;
        else {
            ChatMember currentChatMember = new ChatMember();
            currentChatMember.setMemberId(currentUser.getId());
            ChatMember destinationChatMember = new ChatMember();
            destinationChatMember.setMemberId(dtoChatMessageFromJSON.getDestinationUserId());
            Room newRoom = new Room();
            newRoom.setChatType(ChatType.PRIVATE); // REWORK THIS HARDCODE!!!
            newRoom = chatRoomRepository.save(newRoom);
            currentChatMember.setRoom(newRoom);
            destinationChatMember.setRoom(newRoom);
            chatMemberRepository.save(currentChatMember);
            chatMemberRepository.save(destinationChatMember);

            roomDestination = "/topic/chat/" + newRoom.getId();
        }

        simpMessagingTemplate.convertAndSend("/topic/chatListener", roomDestination);
    }
}
