package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.Lesson;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.LessonRepository;
import kpfu.itis.kasimov.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.UserLesson;
import kpfu.itis.kasimov.repositories.UserLessonRepository;
import kpfu.itis.kasimov.dto.UserLessonDTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserLessonService {

    private final UserLessonRepository userLessonRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final UserProgressService userProgressService;

    public void markAsCompleted(Integer userId, Integer lessonId) {

        User user = userRepository.findById(userId).orElseThrow();
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();

        Optional<UserLesson> existing = userLessonRepository.findByUser_IdAndLesson_Id(userId, lessonId);
        if (existing.isPresent()) {
            return;
        }

        UserLesson userLesson = new UserLesson();
        userLesson.setUser(user);
        userLesson.setLesson(lesson);
        userLesson.setCompleted(true);
        userLesson.setLastUpdated(new Timestamp(System.currentTimeMillis()));

        userLessonRepository.save(userLesson);

        // Update UserProgress
        int completedLessons = userLessonRepository.countCompletedLessons(userId, lesson.getCourse().getId());
        int totalLessons = lessonRepository.findByCourse_Id(lesson.getCourse().getId()).size();

        userProgressService.updateProgress(userId, lesson.getCourse().getId(), completedLessons, totalLessons);
    }

    @Transactional(readOnly = true)
    public Optional<UserLessonDTO> findByUserIdAndLessonId(Integer userId, Integer lessonId) {
        return userLessonRepository.findByUser_IdAndLesson_Id(userId, lessonId)
                .map(UserLessonDTO::valueOf);
    }

    public int countCompletedLessons(Integer userId, Integer courseId) {
        return userLessonRepository.countCompletedLessons(userId, courseId);
    }
}
