package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.Lesson;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.LessonRepository;
import kpfu.itis.kasimov.repositories.UserRepository;
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
public class UserLessonService {
    private final UserLessonRepository userLessonRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    @Autowired
    public UserLessonService(UserLessonRepository userLessonRepository, UserRepository userRepository, LessonRepository lessonRepository) {
        this.userLessonRepository = userLessonRepository;
        this.userRepository = userRepository;
        this.lessonRepository = lessonRepository;
    }

    public List<UserLessonDTO> findByUserId(Integer userId) {
        return userLessonRepository.findByUser_Id(userId).stream().map(UserLessonDTO::valueOf).toList();
    }

    public void markAsCompleted(Integer userId, Integer lessonId) {
        User user = userRepository.findById(userId).orElseThrow();
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        UserLesson userLesson = new UserLesson();
        userLesson.setUser(user);
        userLesson.setLesson(lesson);
        userLesson.setCompleted(true);
        userLesson.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        userLessonRepository.save(userLesson);
    }

    public Optional<UserLessonDTO> findByUserIdAndLessonId(Integer userId, Integer lessonId) {
        return userLessonRepository.findByUser_IdAndLesson_Id(userId, lessonId)
                .map(UserLessonDTO::valueOf);
    }

    public int countCompletedLessons(Integer userId, Integer courseId) {
        return userLessonRepository.countCompletedLessons(userId, courseId);
    }
}
