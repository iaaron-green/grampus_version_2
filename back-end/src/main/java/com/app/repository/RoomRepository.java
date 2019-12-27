package com.app.repository;

import com.app.DTO.DTOChatSendMsg;
import com.app.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

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

    @Query(value = "SELECT room_id FROM chat_members WHERE member_id = :currentId ", nativeQuery = true)
    List<Long> getAllRoomsIdByCurrentMemberId(@Param("currentId") Long currentId, Pageable p);

    Room getById(Long roomId);
}
