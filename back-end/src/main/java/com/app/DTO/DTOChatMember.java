package com.app.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DTOChatMember {

    private Long senderId;
    private Long receiverId;

    private String message;

}
