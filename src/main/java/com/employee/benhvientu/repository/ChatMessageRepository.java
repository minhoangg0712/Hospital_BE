package com.employee.benhvientu.repository;
 import com.employee.benhvientu.entity.ChatSession;
 import com.employee.benhvientu.entity.ChatMessage;
 import org.springframework.data.jpa.repository.JpaRepository;

 import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySession(ChatSession session);
}
