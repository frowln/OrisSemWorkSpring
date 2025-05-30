package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.dto.UserLessonDTO;
import kpfu.itis.kasimov.dto.UserProgressDTO;
import kpfu.itis.kasimov.models.UserProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProgressService {
    private final UserLessonService userLessonService;
    private final UserProgressService userProgressService;
    private final LessonService lessonService;

    @Autowired
    public ProgressService(UserLessonService userLessonService,
                               UserProgressService userProgressService,
                               LessonService lessonService) {
        this.userLessonService = userLessonService;
        this.userProgressService = userProgressService;
        this.lessonService = lessonService;
    }

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

    public Optional<UserProgress> getProgress(Integer userId, Integer courseId) {
        return userProgressService.getEntityByUserAndCourse(userId, courseId);
    }

}
