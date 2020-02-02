package com.app.DTO;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Calendar;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DTOChatSendMsg {

    private Long profileId;
    private String profilePicture;
    private String profileFullName;
    private Calendar createDate;
    private String message;

    public DTOChatSendMsg(Long profileId, String profilePicture, String profileFullName, Calendar createDate, String message) {
        this.profileId = profileId;
        this.profilePicture = profilePicture;
        this.profileFullName = profileFullName;
        this.createDate = createDate;
        this.message = message;
    }
}
