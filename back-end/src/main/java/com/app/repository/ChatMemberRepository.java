package com.app.repository;

import com.app.entities.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {


    @Query(
            value = "SELECT DISTINCT room_id FROM chat_members LEFT JOIN rooms ON rooms.id = chat_members.room_id " +
                    "WHERE member_id IN(:currentId, :destinationId) and rooms.chat_type = :chatType;",
            nativeQuery = true)
    Long getRoomIdByMembersIdAndChatType(@Param("currentId") Long currentId, @Param("destinationId") Long destinationId,
                                         @Param("chatType") String chatType);
}
