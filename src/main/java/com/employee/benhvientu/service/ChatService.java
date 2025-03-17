package com.employee.benhvientu.service;

import com.employee.benhvientu.entity.ChatSession;
import com.employee.benhvientu.entity.ChatMessage;
import com.employee.benhvientu.repository.ChatSessionRepository;
import com.employee.benhvientu.repository.ChatMessageRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatSessionRepository chatSessionRepository, ChatMessageRepository chatMessageRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    // Tạo một phiên trò chuyện mới (hoặc lấy phiên đã tồn tại)
    public ChatSession createOrGetChatSession(Long patientId, Long doctorId, Long departmentId) {
        return chatSessionRepository.findByPatientIdOrDoctorId(patientId, doctorId)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    ChatSession newSession = new ChatSession();
                    newSession.setPatientId(patientId);
                    newSession.setDoctorId(doctorId);
                    newSession.setDepartmentId(departmentId);
                    return chatSessionRepository.save(newSession);
                });
    }

    // Lấy danh sách tin nhắn trong một phiên
    public List<ChatMessage> getMessages(Long sessionId) {
        Optional<ChatSession> session = chatSessionRepository.findById(sessionId);
        return session.map(chatMessageRepository::findBySession).orElse(List.of());
    }

    // Gửi tin nhắn
    @Transactional
    public ChatMessage sendMessage(Long sessionId, Long senderId, Long receiverId, String messageText) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Chat session not found"));

        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMessageText(messageText);
        return chatMessageRepository.save(message);
    }
}
