package kpfu.itis.kasimov.controllers;

import jakarta.validation.Valid;
import kpfu.itis.kasimov.dto.CourseDTO;
import kpfu.itis.kasimov.dto.UserProgressDTO;
import kpfu.itis.kasimov.models.*;
import kpfu.itis.kasimov.security.CustomUserDetails;
import kpfu.itis.kasimov.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final UserProgressService userProgressService;

    // Создание курса
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/new")
    public String showCreateForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getPerson();
        model.addAttribute("user", user);
        model.addAttribute("courseDTO", new CourseDTO());

        return "courses/addCourse";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/new")
    public String createCourse(
            @ModelAttribute("courseDTO") @Valid CourseDTO courseDTO,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User teacher = userDetails.getPerson();
        // Если в DTO есть ошибки валидации – вернём тот же шаблон, чтобы показать ошибки
        if (result.hasErrors()) {
            return "courses/addCourse";
        }

        // Преобразуем DTO → сущность Course
        Course courseEntity = new Course();
        courseEntity.setName(courseDTO.getName());
        courseEntity.setDescription(courseDTO.getDescription());
        courseEntity.setTeacherId(teacher.getId());
        // При создании нового курса у averageRating можем установить 0.0 или null
        courseEntity.setAverageRating(0.0);

        // Сохраняем сущность через сервис
        courseService.save(courseEntity);

        // После сохранения у courseEntity.getId() будет корректное значение (генерируется БД)
        return "redirect:/courses/" + courseEntity.getId();
    }

    // Просмотр курса
    @GetMapping("/{id}")
    public String showCourse(
            @PathVariable Integer id,
            Model model,
            Authentication authentication
    ) {
        // Получаем текущего User (или null, если не аутентифицирован)
        User user = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            user = ((CustomUserDetails) authentication.getPrincipal()).getPerson();
        }

        // Загружаем сам курс
        Course courseEntity = courseService.findById(id);
        if (courseEntity == null) {
            return "error/404";
        }

        // Список уроков для этого курса
        List<Lesson> lessons = lessonService.findByCourseId(id);

        // Список отзывов для этого курса
        List<Review> reviews = reviewService.findByCourseId(id);

        // Информация о преподавателе
        User teacher = userService.findById(courseEntity.getTeacherId()).orElse(null);

        // Проверяем, записан ли текущий пользователь на курс
        boolean enrolled = false;
        if (user != null) {
            enrolled = userCourseService.isEnrolled(user.getId(), id);
        }

        // Считаем общее число студентов, записанных на курс
        int enrolledStudents = userCourseService.countEnrolled(id);


        // Собираем DTO курса для передачи в Thymeleaf
        CourseDTO course = CourseDTO.valueOf(courseEntity);

        // Подготовка "progress": если пользователь записан, пытаемся получить из БД или создаём новый
        UserProgressDTO progressDto = null;
        if (user != null && enrolled) {
            int totalLessons = lessons.size();
            if (totalLessons > 0) {
                Optional<UserProgressDTO> optionalProgress =
                        userProgressService.findByUserIdAndCourseId(user.getId(), id);
                if (optionalProgress.isPresent()) {
                    progressDto = optionalProgress.get();
                } else {
                    // Если записи о прогрессе ещё нет, создаём DTO с нулевым завершением,
                    // но только если есть хотя бы один урок
                    progressDto = new UserProgressDTO(
                            null,
                            user.getId(),
                            id,
                            0,
                            totalLessons,
                            null
                    );
                }
            }
            // Если totalLessons == 0, progressDto останется null
        }

        // Кладём всё в модель
        model.addAttribute("user", user);
        model.addAttribute("course", course);
        model.addAttribute("lessons", lessons);
        model.addAttribute("reviews", reviews);
        model.addAttribute("teacher", teacher);
        model.addAttribute("enrolled", enrolled);
        model.addAttribute("enrolledStudents", enrolledStudents);
        model.addAttribute("progress", progressDto);

        return "courses/course";
    }



    // Редактирование курса
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User teacher = userDetails.getPerson();
        Course course = courseService.findById(id);
        if (course == null || !course.getTeacherId().equals(teacher.getId())) {
            return "error/403";
        }

        model.addAttribute("user", teacher);
        model.addAttribute("course", course);
        return "courses/editCourse";
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{id}/edit")
    public String updateCourse(
            @PathVariable Integer id,
            @ModelAttribute("course") @Valid Course updatedCourse,
            BindingResult result,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User teacher = userDetails.getPerson();
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
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User teacher = userDetails.getPerson();
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
    public String myCourses(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = customUserDetails.getPerson();

        model.addAttribute("user", user);

        String role = user.getRole();
        if ("ROLE_TEACHER".equalsIgnoreCase(role)) {
            List<CourseDTO> createdCourses = courseService.findByTeacherId(user.getId());
            model.addAttribute("createdCourses", createdCourses);
        } else {
            List<CourseDTO> enrolledCourses = courseService.findAll().stream()
                    .filter(course -> userCourseService.isEnrolled(user.getId(), course.getId()))
                    .map(CourseDTO::valueOf)
                    .collect(Collectors.toList());

            model.addAttribute("enrolledCourses", enrolledCourses);
        }

        return "courses/my-course";
    }

    // Управление курсом (страница manage)
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}/manage")
    public String manageCourse(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User teacher = userDetails.getPerson();
        Course courseEntity = courseService.findById(id);

        if (courseEntity == null || !courseEntity.getTeacherId().equals(teacher.getId())) {
            return "error/403";
        }

        List<Lesson> lessons = lessonService.findByCourseId(id);
        List<User> students = userCourseService.findStudentsByCourseId(id);

        model.addAttribute("user", teacher);
        model.addAttribute("course", courseEntity);
        model.addAttribute("lessons", lessons);
        model.addAttribute("students", students);
        model.addAttribute("teacherName", teacher.getName());

        return "courses/manageCourse";
    }

    // Отчислить студента
    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/{courseId}/remove-student")
    public String removeStudentFromCourse(
            @PathVariable Integer courseId,
            @RequestParam Integer studentId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User teacher = userDetails.getPerson();
        Course course = courseService.findById(courseId);
        if (course != null && course.getTeacherId().equals(teacher.getId())) {
            userCourseService.removeStudentFromCourse(courseId, studentId);
        }
        return "redirect:/courses/" + courseId + "/manage";
    }



}