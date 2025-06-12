package kpfu.itis.kasimov.controllers;

import kpfu.itis.kasimov.models.Lesson;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.CourseService;
import kpfu.itis.kasimov.services.LessonService;
import kpfu.itis.kasimov.services.ProgressService;
import kpfu.itis.kasimov.services.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserCourseService userCourseService;

    // Добавить урок
    @GetMapping("/new")
    public String showAddForm(@RequestParam Integer courseId, Model model, Principal principal) {
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        User user = userDetails.getPerson();
        model.addAttribute("user", user);
        model.addAttribute("courseId", courseId);
        model.addAttribute("lesson", new Lesson());
        return "lessons/create";
    }

    @PostMapping("/new")
    public String addLesson(@ModelAttribute("lesson") Lesson lesson, @RequestParam Integer courseId) {
        lesson.setCourse(courseService.findById(courseId));
        lessonService.save(lesson);
        return "redirect:/courses/" + courseId;
    }

    // Показать урок
    @GetMapping("/{id}")
    public String showLesson(@PathVariable Integer id, Model model, Principal principal) {
        Lesson lesson = lessonService.findById(id).orElseThrow();

        boolean completed = false;
        boolean enrolled = false;
        Integer courseId = lesson.getCourse().getId();

        if (principal != null) {
            CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
            User user = userDetails.getPerson();

            // вот правильная проверка:
            enrolled = userCourseService.isEnrolled(user.getId(), courseId);

            if (enrolled) {
                completed = progressService.isCompleted(user.getId(), id);
            }

            model.addAttribute("user", user); // Добавляем user для шаблона
        }

        model.addAttribute("lesson", lesson);
        model.addAttribute("completed", completed);
        model.addAttribute("enrolled", enrolled);

        return "lessons/show";
    }


    // Отметить как завершённый
    @PostMapping("/{id}/complete")
    public String completeLesson(@PathVariable Integer id, Principal principal) {
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        User user = userDetails.getPerson();
        Lesson lesson = lessonService.findById(id).orElseThrow();
        progressService.markAsCompleted(user.getId(), id, lesson.getCourse().getId());
        return "redirect:/lessons/" + id;
    }

    // Страница редактирования урока
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/edit")
    public String showEditLessonForm(
            @PathVariable Integer id,
            @RequestParam Integer courseId,
            Model model, Principal principal
    ) {
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        User user = userDetails.getPerson();
        model.addAttribute("user", user);
        Lesson lesson = lessonService.findById(id).orElseThrow();
        model.addAttribute("lesson", lesson);
        model.addAttribute("courseId", courseId);
        return "lessons/editLesson";
    }

    // Сохранение изменений урока
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/edit")
    public String updateLesson(
            @PathVariable Integer id,
            @RequestParam Integer courseId,
            @ModelAttribute("lesson") Lesson updatedLesson
    ) {
        Lesson existingLesson = lessonService.findById(id).orElseThrow();

        existingLesson.setName(updatedLesson.getName());
        existingLesson.setBody(updatedLesson.getBody());

        lessonService.save(existingLesson);

        return "redirect:/courses/" + courseId + "/manage";
    }

    // Удаление урока
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/delete")
    public String deleteLesson(
            @PathVariable Integer id,
            @RequestParam Integer courseId,
            Principal principal,
            Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) ((Authentication) principal).getPrincipal();
        User user = userDetails.getPerson();
        model.addAttribute("user", user);
        lessonService.deleteById(id);
        return "redirect:/courses/" + courseId + "/manage";
    }

}

