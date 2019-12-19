package com.app.services;

import com.app.enums.ChatType;
import org.springframework.stereotype.Service;

public interface ChatMemberService {

    Long getRoomIdByMembersIdAndChatType(Long currentId, Long destinationId, String chatType);
}
