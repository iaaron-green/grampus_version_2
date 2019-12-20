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
public class DTOChatInit {

    private Long destinationUserId;
    private String textMessage;
    private Long roomId;
    private String chatType;

    public DTOChatInit(Long destinationUserId, String textMessage, Long roomId, String chatType) {
        this.destinationUserId = destinationUserId;
        this.textMessage = textMessage;
        this.roomId = roomId;
        this.chatType = chatType;
    }
}
