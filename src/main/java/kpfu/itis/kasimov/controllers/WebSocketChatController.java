package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.ChatMessage;
import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.ChatMessageService;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Controller;

import java.sql.Timestamp;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatMessageService chatMessageService;
    private final CourseService courseService;
    private final UserService userService;

    @MessageMapping("/chat/{courseId}")
    @SendTo("/topic/course.{courseId}")
    public ChatMessageDTO sendMessage(@DestinationVariable Integer courseId, ChatMessageDTO messageDto) {
        // сохраняем в БД
        Course course = courseService.findById(courseId);
        User sender = userService.findByName(messageDto.getSender()); // или другой способ получить User по name

        ChatMessage chatMessage = ChatMessage.builder()
                .course(course)
                .sender(sender)
                .content(messageDto.getContent())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .build();

        chatMessageService.save(chatMessage);

        // возвращаем обратно DTO
        return messageDto;
    }

    @Data
    public static class ChatMessageDTO {
        private String sender;
        private String content;
    }
}
