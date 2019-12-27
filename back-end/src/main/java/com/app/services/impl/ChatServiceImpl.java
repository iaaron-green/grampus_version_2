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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
        List<DTOChatSendMsgWithMillis> chatMessagesWithMillis = new ArrayList<>();
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

            Page<DTOChatSendMsg> chatMessages = chatMessageRepository.getMessagesByRoomId(currentRoomId, pageRequest(dtoChatInit.getPage(), dtoChatInit.getSize()));
            chatMessages.forEach(msg -> {
                chatMessagesWithMillis.add(new DTOChatSendMsgWithMillis(msg.getProfileId(), msg.getProfilePicture(),
                        msg.getProfileFullName(), msg.getCreateDate().getTimeInMillis(), msg.getMessage()));
            });
        }

        simpMessagingTemplate.convertAndSend("/topic/chatListener"+currentUser.getId(),
                new Gson().toJson(new DTOChatData(currentUser.getId(), dtoChatInit.getTargetUserId(), roomURL, chatMessagesWithMillis)));
    }


    @Override
    public void sendMessage(String dtoChatMessage, String principalName) throws CustomException {

        User loggedUser = userRepository.findByEmail(principalName);
        if (loggedUser == null) {
            //throw new CustomException();
        }

        DTOChatReceivedMsg dtoChatReceivedMsgFromJSON = new Gson().fromJson(dtoChatMessage, DTOChatReceivedMsg.class);

        Room chatRoom = roomRepository.getById(dtoChatReceivedMsgFromJSON.getRoomId());

        if (chatRoom == null){
            throw new CustomException(messageSource.getMessage("chat.room.not.exist", null,
                    LocaleContextHolder.getLocale()), Errors.CHAT_ROOM_NOT_EXIST);
        } else {
            ChatMessage message = new ChatMessage(loggedUser.getId(), dtoChatReceivedMsgFromJSON.getTextMessage(), chatRoom);
            message = chatMessageRepository.save(message);

            DTOChatSendMsgWithMillis dtoChatSendMsgWithMillis = new DTOChatSendMsgWithMillis(loggedUser.getId(), loggedUser.getProfile().getProfilePicture(),
                    loggedUser.getFullName(), message.getCreateDate().getTimeInMillis(), message.getMessage());

            simpMessagingTemplate.convertAndSend("/topic/chat" + chatRoom.getId(), new Gson().toJson(dtoChatSendMsgWithMillis));
            simpMessagingTemplate.convertAndSend("/topic/chatListener" + dtoChatReceivedMsgFromJSON.getTargetUserId(), new Gson().toJson(dtoChatSendMsgWithMillis));
        }
    }

    @Override
    public void getMessagesByPage(String chatMessagesPagination) throws CustomException {

        DTOChatMsgPagination dtoChatMsgPagination = validateDTOChatMessagesPagination(chatMessagesPagination);

        Page<DTOChatSendMsg> chatMessages = chatMessageRepository.getMessagesByRoomId(dtoChatMsgPagination.getRoomId(),
                pageRequest(dtoChatMsgPagination.getPage(), dtoChatMsgPagination.getSize()));

        List<DTOChatSendMsgWithMillis> chatMessagesWithMillis = new ArrayList<>();

        chatMessages.forEach(msg -> {
            chatMessagesWithMillis.add(new DTOChatSendMsgWithMillis(msg.getProfileId(), msg.getProfilePicture(),
                    msg.getProfileFullName(), msg.getCreateDate().getTimeInMillis(), msg.getMessage()));
        });

        simpMessagingTemplate.convertAndSend("/topic/chat" + dtoChatMsgPagination.getRoomId(),
                new Gson().toJson(chatMessagesWithMillis));
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

    private DTOChatMsgPagination validateDTOChatMessagesPagination(String input) throws CustomException {

        DTOChatMsgPagination dtoChatMsgPagination;

        try {
            dtoChatMsgPagination = new Gson().fromJson(input, DTOChatMsgPagination.class);
        } catch (Exception e) {
            throw new CustomException(messageSource.getMessage("wrong.input.data.chat", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_INPUT_DATA_CHAT);
        }

        if (dtoChatMsgPagination.getRoomId() == null || dtoChatMsgPagination.getRoomId() == 0) {
            throw new CustomException(messageSource.getMessage("chat.room.not.exist", null,
                    LocaleContextHolder.getLocale()), Errors.CHAT_ROOM_NOT_EXIST);
        }

        if (dtoChatMsgPagination.getPage() == null) {
            throw new CustomException(messageSource.getMessage("wrong.page.as.parameter", null,
                    LocaleContextHolder.getLocale()), Errors.WRONG_PAGE_AS_PARAMETER);
        }

        if (dtoChatMsgPagination.getSize() == null || dtoChatMsgPagination.getSize() == 0) {
            dtoChatMsgPagination.setSize(Constants.DEFAULT_SIZE_MESSAGE_HISTORY);
        }
        return dtoChatMsgPagination;
    }

    private Pageable pageRequest(int page, int size) {
        return PageRequest.of(page, size);
    }
}
