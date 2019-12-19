package com.app.DTO;

import com.app.enums.ChatType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatMessage {

    private Long destinationUserId;
    private String textMessage;
    private Long roomId;
    private ChatType chatType;

    public DTOChatMessage(Long destinationUserId, String textMessage, Long roomId, ChatType chatType) {
        this.destinationUserId = destinationUserId;
        this.textMessage = textMessage;
        this.roomId = roomId;
        this.chatType = chatType;
    }
}
