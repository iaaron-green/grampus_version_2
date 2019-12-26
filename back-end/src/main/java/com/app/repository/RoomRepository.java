package com.app.repository;

import com.app.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query(
            value = "SELECT room_id FROM chat_members JOIN rooms ON rooms.id = chat_members.room_id " +
                    "WHERE member_id = :currentId " +
                    "AND room_id IN(SELECT room_id FROM chat_members where member_id = :targetId) " +
                    "and rooms.chat_type = :chatType",

            nativeQuery = true)
    Long getRoomIdByMembersIdAndChatType(@Param("currentId") Long currentId, @Param("targetId") Long targetId,
                                         @Param("chatType") String chatType);

    Room getById(Long roomId);
}
