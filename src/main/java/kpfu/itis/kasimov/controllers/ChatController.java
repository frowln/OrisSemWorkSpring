package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.ChatMessage;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageService chatMessageService;

    @GetMapping("/{courseId}")
    public String chatPage(@PathVariable Integer courseId, Model model, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getPerson();

        model.addAttribute("user", user);
        model.addAttribute("courseId", courseId);

        List<ChatMessage> messages = chatMessageService.getMessagesForCourse(courseId);
        model.addAttribute("messages", messages);

        return "chat/chat";
    }

}
