package kpfu.itis.kasimov.controllers;

import jakarta.validation.Valid;
import kpfu.itis.kasimov.dto.CourseDTO;
import kpfu.itis.kasimov.models.*;
import kpfu.itis.kasimov.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final LessonService lessonService;
    private final ReviewService reviewService;
    private final UserCourseService userCourseService;

    // Создание курса
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        return "courses/create";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/new")
    public String createCourse(
            @ModelAttribute("course") @Valid Course course,
            BindingResult result,
            @AuthenticationPrincipal User teacher
    ) {
        if (result.hasErrors()) return "courses/create";
        course.setTeacherId(teacher.getId());
        courseService.save(course);
        return "redirect:/courses/" + course.getId();
    }

    // Просмотр курса
    @GetMapping("/{id}")
    public String showCourse(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal User user
    ) {
        Course course = courseService.findById(id);
        if (course == null) return "error/404";

        List<Lesson> lessons = lessonService.findByCourseId(id);
        List<Review> reviews = reviewService.findByCourseId(id);
        User teacher = userService.findById(course.getTeacherId()).orElse(null);
        boolean enrolled = user != null && userCourseService.isEnrolled(user.getId(), id);

        model.addAttribute("course", CourseDTO.valueOf(course));
        model.addAttribute("lessons", lessons);
        model.addAttribute("reviews", reviews);
        model.addAttribute("teacher", teacher);
        model.addAttribute("enrolled", enrolled);
        model.addAttribute("user", user);

        return "courses/show";
    }

    // Редактирование курса
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal User teacher
    ) {
        Course course = courseService.findById(id);
        if (course == null || !course.getTeacherId().equals(teacher.getId())) {
            return "error/403";
        }
        model.addAttribute("course", course);
        return "courses/edit";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/edit")
    public String updateCourse(
            @PathVariable Integer id,
            @ModelAttribute("course") @Valid Course updatedCourse,
            BindingResult result,
            @AuthenticationPrincipal User teacher
    ) {
        if (result.hasErrors()) return "courses/edit";

        Course existingCourse = courseService.findById(id);
        if (existingCourse == null || !existingCourse.getTeacherId().equals(teacher.getId())) {
            return "error/403";
        }

        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDescription(updatedCourse.getDescription());
        courseService.save(existingCourse);

        return "redirect:/courses/" + id;
    }

    // Удаление курса
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/delete")
    public String deleteCourse(
            @PathVariable Integer id,
            @AuthenticationPrincipal User teacher
    ) {
        Course course = courseService.findById(id);
        if (course != null && course.getTeacherId().equals(teacher.getId())) {
            courseService.deleteById(id);
        }
        return "redirect:/courses";
    }

    // Поиск курсов
    @GetMapping("/search")
    public String searchCourses(
            @RequestParam String query,
            Model model
    ) {
        List<CourseDTO> courses = courseService.searchByName(query);
        model.addAttribute("courses", courses);
        model.addAttribute("query", query);
        return "courses/search";
    }

    // Мои курсы
    @GetMapping
    public String myCourses(
            Model model,
            @AuthenticationPrincipal User user
    ) {
        if (user.getRole().equals("ST