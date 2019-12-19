package com.app.services.impl;

import com.app.repository.ChatMemberRepository;
import com.app.services.ChatMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMemberServiceImpl implements ChatMemberService {

    @Autowired
    private ChatMemberRepository chatMemberRepository;

    @Override
    public Long getRoomIdByMembersIdAndChatType(Long currentId, Long destinationId, String chatType) {
        return chatMemberRepository.getRoomIdByMembersIdAndChatType(currentId, destinationId, chatType);
    }
}
