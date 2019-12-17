package com.app.services;

import com.app.entities.Room;
import org.springframework.stereotype.Service;

@Service
public interface RoomService {
    Room getOrCreate(Long chatMemberId1, Long chatMemberId2);
}
