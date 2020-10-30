package com.belogrudov.javabot.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersTable extends JpaRepository<User, Integer> {
    Boolean existsByChatId(Long chatId);

    User findByChatId(Long chatId);

}
