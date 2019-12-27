package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatSendMsgWithMillis {

    private Long profileId;
    private String profilePicture;
    private String profileFullName;
    private Long createDate;
    private String message;

    public DTOChatSendMsgWithMillis(Long profileId, String profilePicture, String profileFullName, Long createDate, String message) {
        this.profileId = profileId;
        this.profilePicture = profilePicture;
        this.profileFullName = profileFullName;
        this.createDate = createDate;
        this.message = message;
    }
}
