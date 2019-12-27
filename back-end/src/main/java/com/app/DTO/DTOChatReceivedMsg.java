package com.app.DTO;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatReceivedMsg {

    private String textMessage;
    private Long roomId;
    private Long targetUserId;

    public DTOChatReceivedMsg(String textMessage, Long roomId, Long targetUserId) {
        this.textMessage = textMessage;
        this.roomId = roomId;
        this.targetUserId = targetUserId;
    }
}