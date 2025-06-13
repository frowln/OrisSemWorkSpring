package kpfu.itis.kasimov.controllers;

import jakarta.validation.Valid;
import kpfu.itis.kasimov.models.Lesson;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService    lessonService;
    private final CourseService    courseService;
    private final ProgressService  progressService;
    private final UserCourseService userCourseService;

    /*─────────────────────  СОЗДАТЬ УРОК  ─────────────────────*/

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/new")
    public String showAddForm(
            @RequestParam Integer courseId,
            Model model,
            @AuthenticationPrincipal(expression = "user") User current      // ***
    ) {
        model.addAttribute("user", current);
        model.addAttribute("courseId", courseId);
        model.addAttribute("lesson", new Lesson());
        return "lessons/create";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/new")
    public String addLesson(
            @ModelAttribute("lesson") Lesson lesson,
            @RequestParam Integer courseId
    ) {
        lesson.setCourse(courseService.findById(courseId));
        lessonService.save(lesson);
        return "redirect:/courses/" + courseId;
    }

    /*──────────────────────  ПОКАЗ УРОКА  ─────────────────────*/

    @GetMapping("/{id}")
    public String showLesson(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal(expression = "user") User current      // ***
    ) {
        Lesson lesson   = lessonService.findById(id).orElseThrow();
        Integer courseId = lesson.getCourse().getId();

        boolean enrolled  = current != null && userCourseService.isEnrolled(current.getId(), courseId);
        boolean completed = enrolled     && progressService.isCompleted(current.getId(), id);

        model.addAttribute("user", current);
        model.addAttribute("lesson", lesson);
        model.addAttribute("enrolled", enrolled);
        model.addAttribute("completed", completed);
        return "lessons/show";
    }

    /*──────────────  ОТМЕТИТЬ «ЗАВЕРШЁН»  ───────────────*/

    @PostMapping("/{id}/complete")
    public String completeLesson(
            @PathVariable Integer id,
            @AuthenticationPrincipal(expression = "user") User current      // ***
    ) {
        Lesson lesson = lessonService.findById(id).orElseThrow();
        progressService.markAsCompleted(current.getId(), id, lesson.getCourse().getId());
        return "redirect:/lessons/" + id;
    }

    /*───────────────────  РЕДАКТИРОВАНИЕ  ──────────────────*/

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            @RequestParam Integer courseId,
            Model model,
            @AuthenticationPrincipal(expression = "user") User current      // ***
    ) {
        model.addAttribute("user", current);
        model.addAttribute("lesson", lessonService.findById(id).orElseThrow());
        model.addAttribute("courseId", courseId);
        return "lessons/editLesson";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/edit")
    public String updateLesson(
            @PathVariable Integer id,
            @RequestParam Integer courseId,
            @ModelAttribute("lesson") @Valid Lesson updated
    ) {
        Lesson origin = lessonService.findById(id).orElseThrow();
        origin.setName(updated.getName());
        origin.setBody(updated.getBody());
        lessonService.save(origin);
        return "redirect:/courses/" + courseId + "/manage";
    }

    /*──────────────────────  УДАЛЕНИЕ  ─────────────────────*/

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/delete")
    public String deleteLesson(
            @PathVariable Integer id,
            @RequestParam Integer courseId
    ) {
        lessonService.deleteById(id);
        return "redirect:/courses/" + courseId + "/manage";
    }
}
