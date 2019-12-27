package com.app.repository;

import com.app.DTO.DTOChatSendMsg;
import com.app.entities.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT NEW com.app.DTO.DTOChatSendMsg(p.id, p.profilePicture, u.fullName, cm.createDate, cm.message) " +
            "FROM  Profile p JOIN ChatMessage cm ON p.id = cm.userId " +
            "JOIN User u ON u.id = cm.userId WHERE cm.room.id = :roomId ORDER BY cm.createDate DESC")
    Page<DTOChatSendMsg> getMessagesByRoomId(@Param("roomId") Long roomId, Pageable p);

    @Query("SELECT NEW com.app.DTO.DTOChatSendMsg(p.id, p.profilePicture, u.fullName, cm.createDate, cm.message) " +
            "FROM  Profile p JOIN ChatMessage cm ON p.id = cm.userId " +
            "JOIN User u ON u.id = cm.userId WHERE cm.createDate IN(SELECT MAX(cm.createDate) " +
            "FROM ChatMessage cm WHERE cm.room.id IN (:chatRoomsByCurrentUser))")
    List<DTOChatSendMsg> getAllLastMessagesForChatRoomsByRoomsId(@Param("chatRoomsByCurrentUser") List<Long> chatRoomsByCurrentUser);

}
