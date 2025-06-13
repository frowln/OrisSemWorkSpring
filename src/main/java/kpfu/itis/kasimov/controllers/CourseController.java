package kpfu.itis.kasimov.controllers;

import jakarta.validation.Valid;
import kpfu.itis.kasimov.dto.CourseDTO;
import kpfu.itis.kasimov.dto.UserProgressDTO;
import kpfu.itis.kasimov.models.*;
import kpfu.itis.kasimov.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService       courseService;
    private final UserService         userService;
    private final LessonService       lessonService;
    private final ReviewService       reviewService;
    private final UserCourseService   userCourseService;
    private final UserProgressService userProgressService;

    /*───────────────────────────  СОЗДАНИЕ  ───────────────────────────*/

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/new")
    public String showCreateForm(
            Model model,
            @AuthenticationPrincipal(expression = "user") User current) {     // <-- change
        model.addAttribute("user", current);
        model.addAttribute("courseDTO", new CourseDTO());
        return "courses/addCourse";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/new")
    public String createCourse(
            @ModelAttribute("courseDTO") @Valid CourseDTO courseDTO,
            BindingResult result,
            @AuthenticationPrincipal(expression = "user") User current) {     // <-- change
        if (result.hasErrors()) return "courses/addCourse";

        Course entity = new Course();
        entity.setName(courseDTO.getName());
        entity.setDescription(courseDTO.getDescription());
        entity.setTeacherId(current.getId());
        entity.setAverageRating(0.0);
        courseService.save(entity);

        return "redirect:/courses/" + entity.getId();
    }

    /*───────────────────────────  ПРОСМОТР  ───────────────────────────*/

    @GetMapping("/{id}")
    public String showCourse(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal(expression = "user") User current) {     // <-- change
        Course courseEntity = courseService.findById(id);
        if (courseEntity == null) return "error/404";

        List<Lesson>  lessons  = lessonService.findByCourseId(id);
        List<Review>  reviews  = reviewService.findByCourseId(id);
        User          teacher  = userService.findById(courseEntity.getTeacherId()).orElse(null);
        boolean       enrolled = current != null && userCourseService.isEnrolled(current.getId(), id);
        int           count    = userCourseService.countEnrolled(id);

        CourseDTO course      = CourseDTO.valueOf(courseEntity);
        UserProgressDTO prog  = null;

        if (current != null && enrolled && !lessons.isEmpty()) {
            prog = userProgressService
                    .findByUserIdAndCourseId(current.getId(), id)
                    .orElse(new UserProgressDTO(null, current.getId(),
                            id, 0, lessons.size(), null));
        }

        model.addAttribute("user", current);
        model.addAttribute("course", course);
        model.addAttribute("lessons", lessons);
        model.addAttribute("reviews", reviews);
        model.addAttribute("teacher", teacher);
        model.addAttribute("enrolled", enrolled);
        model.addAttribute("enrolledStudents", count);
        model.addAttribute("progress", prog);
        return "courses/course";
    }

    /*───────────────────────  РЕДАКТИРОВАНИЕ  ─────────────────────────*/

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal(expression = "user") User teacher) {     // <-- change
        Course course = courseService.findById(id);
        if (course == null || !course.getTeacherId().equals(teacher.getId())) return "error/403";

        model.addAttribute("user", teacher);
        model.addAttribute("course", course);
        return "courses/editCourse";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/edit")
    public String updateCourse(
            @PathVariable Integer id,
            @ModelAttribute("course") @Valid Course updated,
            BindingResult result,
            @AuthenticationPrincipal(expression = "user") User teacher) {     // <-- change
        if (result.hasErrors()) return "courses/editCourse";

        Course course = courseService.findById(id);
        if (course == null || !course.getTeacherId().equals(teacher.getId())) return "error/403";

        course.setName(updated.getName());
        course.setDescription(updated.getDescription());
        courseService.save(course);

        return "redirect:/courses/" + id;
    }

    /*──────────────────────────  УДАЛЕНИЕ  ────────────────────────────*/

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/delete")
    public String deleteCourse(
            @PathVariable Integer id,
            @AuthenticationPrincipal(expression = "user") User teacher) {     // <-- change
        Course course = courseService.findById(id);
        if (course != null && course.getTeacherId().equals(teacher.getId())) {
            courseService.deleteById(id);
        }
        return "redirect:/courses";
    }

    /*───────────────────────────  ПОИСК  ──────────────────────────────*/

    @GetMapping("/search")
    public String searchCourses(@RequestParam String query, Model model) {
        model.addAttribute("courses", courseService.searchByName(query));
        model.addAttribute("query",   query);
        return "courses/search";
    }

    /*──────────────────────  “МОИ” КУРСЫ  ────────────────────────────*/

    @GetMapping
    public String myCourses(
            Model model,
            @AuthenticationPrincipal(expression = "user") User current) {    // <-- change
        model.addAttribute("user", current);

        if ("ROLE_TEACHER".equalsIgnoreCase(current.getRole())) {
            model.addAttribute("createdCourses",
                    courseService.findByTeacherId(current.getId()));
        } else {
            List<CourseDTO> enrolled = courseService.findAll().stream()
                    .filter(c -> userCourseService.isEnrolled(current.getId(), c.getId()))
                    .map(CourseDTO::valueOf)
                    .collect(Collectors.toList());
            model.addAttribute("enrolledCourses", enrolled);
        }
        return "courses/my-course";
    }

    /*─────────────────────  УПРАВЛЕНИЕ КУРСОМ  ───────────────────────*/

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/manage")
    public String manageCourse(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal(expression = "user") User teacher) {    // <-- change
        Course course = courseService.findById(id);
        if (course == null || !course.getTeacherId().equals(teacher.getId())) return "error/403";

        model.addAttribute("user",     teacher);
        model.addAttribute("course",   course);
        model.addAttribute("lessons",  lessonService.findByCourseId(id));
        model.addAttribute("students", userCourseService.findStudentsByCourseId(id));
        model.addAttribute("teacherName", teacher.getName());
        return "courses/manageCourse";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{courseId}/remove-student")
    public String removeStudent(
            @PathVariable Integer courseId,
            @RequestParam Integer studentId,
            @AuthenticationPrincipal(expression = "user") User teacher) {    // <-- change
        Course course = courseService.findById(courseId);
        if (course != null && course.getTeacherId().equals(teacher.getId())) {
            userCourseService.removeStudentFromCourse(courseId, studentId);
        }
        return "redirect:/courses/" + courseId + "/manage";
    }

    /*─────────────────────────  ЧАТ  ─────────────────────────────────*/

    @GetMapping("/{id}/chat")
    public String chat(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal(expression = "user") User current) {    // <-- change
        model.addAttribute("user",     current);
        model.addAttribute("courseId", id);
        return "courses/chat";
    }
}
