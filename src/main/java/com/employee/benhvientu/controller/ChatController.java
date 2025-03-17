package com.employee.benhvientu.controller;

import com.employee.benhvientu.entity.ChatMessage;
import com.employee.benhvientu.entity.ChatSession;
import com.employee.benhvientu.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // Tạo hoặc lấy phiên trò chuyện giữa bệnh nhân và bác sĩ
    @PostMapping("/session")
    public ResponseEntity<ChatSession> createSession(@RequestParam Long patientId,
                                                     @RequestParam Long doctorId,
                                                     @RequestParam Long departmentId) {
        ChatSession session = chatService.createOrGetChatSession(patientId, doctorId, departmentId);
        return ResponseEntity.ok(session);
    }

    // Lấy danh sách tin nhắn của một phiên trò chuyện
    @GetMapping("/messages/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long sessionId) {
        return ResponseEntity.ok(chatService.getMessages(sessionId));
    }

    // Gửi tin nhắn
    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestParam Long sessionId,
                                                   @RequestParam Long senderId,
                                                   @RequestParam Long receiverId,
                                                   @RequestParam String messageText) {
        ChatMessage message = chatService.sendMessage(sessionId, senderId, receiverId, messageText);
        return ResponseEntity.ok(message);
    }
}
