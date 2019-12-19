package com.app.WebSocket;

import com.app.DTO.DTOChatMessage;
import com.app.entities.User;
import com.app.repository.UserRepository;
import com.app.services.ChatMemberService;
import com.app.services.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.security.Principal;

@Controller
public class WebSocketChatController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    ChatMemberService chatMemberService;

    @Autowired
    UserRepository userRepository;

    @MessageMapping({"/chat.startChat"})
    public void startChat(String dtoChatMessage, Principal principal) {

        DTOChatMessage dtoChatMessageFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatMessage.class);
        User currentUser = userRepository.findByEmail(principal.getName());


        if (dtoChatMessageFromJSON.getDestinationUserId() == null || StringUtils.isEmpty(dtoChatMessageFromJSON.getChatType())) {
            System.out.println("throw exception wrong input data ");
        }

        Long currentRoomId = chatMemberService.getRoomIdByMembersIdAndChatType(currentUser.getId(),
                dtoChatMessageFromJSON.getDestinationUserId(), dtoChatMessageFromJSON.getChatType());

        String roomDestination = "/topic/chat/" + currentRoomId;

        simpMessagingTemplate.convertAndSend("/topic/chatListener", roomDestination);
    }

    @MessageMapping({"/chat.sendMessage"})
    public void sendMessage(String dtoChatMessage, Principal principal) {

        System.out.println("PRINCIPAL - "+ principal.getName());

        DTOChatMessage dtoChatMessageFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatMessage.class);
        System.out.println(dtoChatMessageFromJSON);

        simpMessagingTemplate.convertAndSend("/topic/chat", "FROM convert");

    }
}
