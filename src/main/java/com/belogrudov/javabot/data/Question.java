package com.belogrudov.javabot.data;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "questions")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "text")
    String question;

    @Column(columnDefinition = "text")
    String description;

    public Question() {
    }

    public Question(String question, String description) {
        this.question = question;
        this.description = description;
    }
}
