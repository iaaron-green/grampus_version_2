package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatData {

    private Long currentUserId;
    private Long targetUserId;
    private String roomURL;
    private List<DTOChatSendMsgWithMillis> chatMessages;

    public DTOChatData(Long currentUserId, Long targetUserId, String roomURL, List<DTOChatSendMsgWithMillis> chatMessages) {
        this.currentUserId = currentUserId;
        this.targetUserId = targetUserId;
        this.roomURL = roomURL;
        this.chatMessages = chatMessages;
    }
}
