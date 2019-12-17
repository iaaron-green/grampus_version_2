package com.app.repository;

import com.app.entities.Room;
import com.app.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room getById(Long id);

    @Override
    List<Room> findAll();

    List<Room> findAllByChatMemberId(Long id);

    Room getByChatMembers1AndChatMembers2(Long chatMemberId1, Long chatMemberId2);

    Room createChat(Long chatMemberId1, Long chatMemberId2);

    List<Room> findByChatType(ChatType type);

}
