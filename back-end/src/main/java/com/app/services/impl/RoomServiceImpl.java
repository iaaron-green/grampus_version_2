package com.app.services.impl;

import com.app.entities.Room;
import com.app.repository.RoomRepository;
import com.app.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    RoomRepository roomRepository;
    @Override
    public Room getOrCreate(Long chatMemberId1, Long chatMemberId2) {
        Room room = roomRepository.getByChatMembers1AndChatMembers2(chatMemberId1, chatMemberId2);
        if (room == null) {
            room = roomRepository.createChat(chatMemberId1, chatMemberId2);
        }

        return room;
    }

}
