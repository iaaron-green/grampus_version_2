package com.app.DTO;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Setter
public class DTOChatMember {

    private Long senderId;
    private Long receiverId;

    private String message;

}
