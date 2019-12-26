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

    private Long targetUserId;
    private Long roomId;
    private ChatType chatType;
    private Integer page;
    private Integer size;

    public DTOChatInit(Long targetUserId, Long roomId, ChatType chatType, Integer page, Integer size) {
        this.targetUserId = targetUserId;
        this.roomId = roomId;
        this.chatType = chatType;
        this.page = page;
        this.size = size;
    }
}
