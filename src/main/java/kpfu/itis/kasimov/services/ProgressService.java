package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.dto.UserLessonDTO;
import kpfu.itis.kasimov.dto.UserProgressDTO;
import kpfu.itis.kasimov.models.UserProgress;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProgressService {

    private final UserLessonService userLessonService;
    private final UserProgressService userProgressService;
    private final LessonService lessonService;

    public boolean isCompleted(Integer userId, Integer lessonId) {
        return userLessonService.findByUserIdAndLessonId(userId, lessonId)
                .map(UserLessonDTO::isCompleted)
                .orElse(false);
    }

    public void markAsCompleted(Integer userId, Integer lessonId, Integer courseId) {
        userLessonService.markAsCompleted(userId, lessonId);

        int totalLessons = lessonService.findByCourseId(courseId).size();
        int completedLessons = userLessonService.countCompletedLessons(userId, courseId);
        userProgressService.updateProgress(userId, courseId, completedLessons, totalLessons);
    }

    @Transactional(readOnly = true)
    public Optional<UserProgress> getProgress(Integer userId, Integer courseId) {
        return userProgressService.getEntityByUserAndCourse(userId, courseId);
    }

}
