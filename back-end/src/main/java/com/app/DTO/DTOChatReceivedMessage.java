package com.app.DTO;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatReceivedMessage {

    private String textMessage;
    private Long roomId;

    public DTOChatReceivedMessage(String textMessage, Long roomId) {
        this.textMessage = textMessage;
        this.roomId = roomId;
    }
}