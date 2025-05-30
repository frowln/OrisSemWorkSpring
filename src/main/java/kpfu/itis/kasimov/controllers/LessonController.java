package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.Lesson;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.LessonService;
import kpfu.itis.kasimov.services.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    private final CourseService courseService;
    private final ProgressService progressService;

    // Добавить урок
    @GetMapping("/new")
    public String showAddForm(@RequestParam Integer courseId, Model model) {
        model.addAttribute("courseId", courseId);
        model.addAttribute("lesson", new Lesson());
        return "lessons/create";
    }

    @PostMapping("/new")
    public String addLesson(@ModelAttribute("lesson") Lesson lesson, @RequestParam Integer courseId) {
        lesson.setCourse(courseService.findById(courseId)); // Получите полный объект Course
        lessonService.save(lesson);
        lessonService.save(lesson);
        return "redirect:/courses/" + courseId;
    }

    // Показать урок
    @GetMapping("/{id}")
    public String showLesson(@PathVariable Integer id, Model model, Principal principal) {
        Lesson lesson = lessonService.findById(id).orElseThrow();
        boolean completed = false;
        if (principal != null) {
            User user = (User) ((Authentication) principal).getPrincipal();
            completed = progressService.isCompleted(user.getId(), id);
        }
        model.addAttribute("lesson", lesson);
        model.addAttribute("completed", completed);
        return "lessons/show";
    }

    // Отметить как завершённый
    @PostMapping("/{id}/complete")
    public String completeLesson(@PathVariable Integer id, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        Lesson lesson = lessonService.findById(id).orElseThrow();
        progressService.markAsCompleted(user.getId(), id, lesson.getCourse().getId());
        return "redirect:/lessons/" + id;
    }
}
