package com.belogrudov.javabot.data;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@Table(name = "questions")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String question;
    String description;

    public Question() {
    }

    public Question(String question, String description) {
        this.question = question;
        this.description = description;
    }
}
