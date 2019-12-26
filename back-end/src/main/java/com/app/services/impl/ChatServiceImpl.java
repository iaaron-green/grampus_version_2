package com.app.services.impl;

import com.app.DTO.*;
import com.app.configtoken.Constants;
import com.app.entities.ChatMember;
import com.app.entities.ChatMessage;
import com.app.entities.Room;
import com.app.entities.User;
import com.app.exceptions.CustomException;
import com.app.exceptions.Errors;
import com.app.repository.ChatMemberRepository;
import com.app.repository.ChatMessageRepository;
import com.app.repository.RoomRepository;
import com.app.repository.UserRepository;
import com.app.services.ChatService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private MessageSource messageSource;

    @Autowired
    public ChatServiceImpl(SimpMessagingTemplate simpMessagingTemplate, ChatMessageRepository chatMessageRepository,
                           RoomRepository roomRepository, UserRepository userRepository, ChatMemberRepository chatMemberRepository,
                           MessageSource messageSource) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.messageSource = messageSource;
    }

    @Override
    public void chatInit(String dto, String currentUserEmail) throws CustomException {

        User currentUser = userRepository.findByEmail(currentUserEmail);
        DTOChatInit dtoChatInit = validateDTOChatInit(dto);

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
    public void sendMessage(String dtoChatMessage, String principalName) throws CustomException {

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
            throw new CustomException(messageSource.getMessage("chat.room.not.exist", null, LocaleContextHolder.getLocale()), Errors.CHAT_ROOM_NOT_EXIST);
        }
    }

    @Override
    public void getMessagesByPage(String chatMessagesPagination) throws CustomException {

        DTOChatMessagesPagination dtoChatMessagesPagination = validateDTOChatMessagesPagination(chatMessagesPagination);

        Page<DTOChatSendMessage> chatMessages = chatMessageRepository.getMessagesByRoomId(dtoChatMessagesPagination.getRoomId(),
                pageRequest(dtoChatMessagesPagination.getPage(), dtoChatMessagesPagination.getSize()));

        simpMessagingTemplate.convertAndSend("/topic/chat" + dtoChatMessagesPagination.getRoomId(),
                new Gson().toJson(chatMessages.getContent()));
    }


    private DTOChatInit validateDTOChatInit(String input) throws CustomException {

        DTOChatInit dtoChatInit;

        try {
            dtoChatInit = new Gson().fromJson(input, DTOChatInit.class);
        } catch (Exception e) {
            throw new CustomException(messageSource.getMessage("wrong.input.data.chat", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_INPUT_DATA_CHAT);
        }

        if (dtoChatInit.getTargetUserId() == null) {
            throw new CustomException(messageSource.getMessage("wrong.chat.target.id", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_CHAT_TARGET_ID);
        }

        if (StringUtils.isEmpty(dtoChatInit.getChatType())) {
            throw new CustomException(messageSource.getMessage("wrong.chat.type", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_CHAT_TYPE);
        }

        if (dtoChatInit.getPage() == null) {
            throw new CustomException(messageSource.getMessage("wrong.page.as.parameter", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_PAGE_AS_PARAMETER);
        }

        if (dtoChatInit.getSize() == null || dtoChatInit.getSize() == 0) {
            dtoChatInit.setSize(Constants.DEFAULT_SIZE_MESSAGE_HISTORY);
        }

        return dtoChatInit;
    }

    private DTOChatMessagesPagination validateDTOChatMessagesPagination(String input) throws CustomException {

        DTOChatMessagesPagination dtoChatMessagesPagination;

        try {
            dtoChatMessagesPagination = new Gson().fromJson(input, DTOChatMessagesPagination.class);
        } catch (Exception e) {
            throw new CustomException(messageSource.getMessage("wrong.input.data.chat", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_INPUT_DATA_CHAT);
        }

        if (dtoChatMessagesPagination.getRoomId() == null || dtoChatMessagesPagination.getRoomId() == 0) {
            throw new CustomException(messageSource.getMessage("chat.room.not.exist", null,
                    LocaleContextHolder.getLocale()), Errors.CHAT_ROOM_NOT_EXIST);
        }

        if (dtoChatMessagesPagination.getPage() == null) {
            throw new CustomException(messageSource.getMessage("wrong.page.as.parameter", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_PAGE_AS_PARAMETER);
        }

        if (dtoChatMessagesPagination.getSize() == null || dtoChatMessagesPagination.getSize() == 0) {
            dtoChatMessagesPagination.setSize(Constants.DEFAULT_SIZE_MESSAGE_HISTORY);
        }
        return dtoChatMessagesPagination;
    }

    private Pageable pageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }
}
