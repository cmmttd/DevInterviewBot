package com.belogrudov.javabot.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long chatId;
    String name;
    @Column(name = "current_q_id")
    Long currentQId;
    String history;

    protected User() {
    }

    public User(Long chatId, String name, Long currentQId, String history) {
        this.chatId = chatId;
        this.name = name;
        this.currentQId = currentQId;
        this.history = history;
    }
}
