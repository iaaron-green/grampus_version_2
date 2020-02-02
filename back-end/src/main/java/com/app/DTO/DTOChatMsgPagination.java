package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatMsgPagination {

    private Integer page;
    private Integer size;
    private Long roomId;
}
