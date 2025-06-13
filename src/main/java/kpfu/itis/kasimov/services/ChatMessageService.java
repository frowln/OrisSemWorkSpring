package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.ChatMessage;
import kpfu.itis.kasimov.repositories.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void save(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getMessagesForCourse(Integer courseId) {
        return chatMessageRepository.findByCourseIdOrderByTimestampAsc(courseId);
    }
}

