package com.app.repository;

import com.app.entities.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
    ChatMember findByRoomId(Long id);

    List<ChatMember> findAllByRoomId(Long roomId);
}
