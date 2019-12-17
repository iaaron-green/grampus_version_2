package com.app.services;


import com.app.entities.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChatMessageService {
    ChatMessage createMessage(Long roomId, Long chatMemberId);
}
