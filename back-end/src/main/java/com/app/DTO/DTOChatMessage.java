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

    private String textMessage;
    private Long roomId;

    public DTOChatMessage(String textMessage, Long roomId) {
        this.textMessage = textMessage;
        this.roomId = roomId;
    }
}
