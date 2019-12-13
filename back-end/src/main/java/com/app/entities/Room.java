package com.app.entities;

import com.app.enums.ChatType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Calendar createDate = Calendar.getInstance();

    private String roomName;

    private ChatType chatType;

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, mappedBy = "room")
    private List<ChatMember> chatMembers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, mappedBy = "room")
    private List<ChatMessage> chatMessages = new ArrayList<>();
}
