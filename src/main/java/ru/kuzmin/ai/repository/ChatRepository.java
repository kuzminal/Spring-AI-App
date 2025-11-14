package ru.kuzmin.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kuzmin.ai.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
