package com.app.DTO;

public class DTOTest {

    private Long currentUserId;
    private Long targetUserId;
    private Long roomId;

    public DTOTest(Long currentUserId, Long targetUserId, Long roomId) {
        this.currentUserId = currentUserId;
        this.targetUserId = targetUserId;
        this.roomId = roomId;
    }
}
