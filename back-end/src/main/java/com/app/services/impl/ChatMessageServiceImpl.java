package com.app.services.impl;

import com.app.entities.ChatMessage;
import com.app.repository.ChatMessageRepository;
import com.app.services.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    ChatMessageRepository chatMessageRepository;


    @Override
    public ChatMessage createMessage(Long roomId, Long chatMemberId) {
        return null;
    }
}
