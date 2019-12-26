package com.app.DTO;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatMember {

    private Long senderId;
    private Long receiverId;

    private String message;

}
