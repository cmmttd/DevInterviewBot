package com.belogrudov.javabot.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Long chatId;
    String name;
    @Column(name = "current_q_id")
    Integer currentQId;
    @ElementCollection(fetch = FetchType.EAGER)
    List<Integer> historyArray;

    protected User() {
    }

    public User(Long chatId, String name, Integer currentQId, List<Integer> historyArray) {
        this.chatId = chatId;
        this.name = name;
        this.currentQId = currentQId;
        this.historyArray = historyArray;
    }
}
