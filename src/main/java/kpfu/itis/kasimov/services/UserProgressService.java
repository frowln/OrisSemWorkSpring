package kpfu.itis.kasimov.services;

import kpfu.itis.kasimov.models.Course;
import kpfu.itis.kasimov.models.User;
import kpfu.itis.kasimov.repositories.CourseRepository;
import kpfu.itis.kasimov.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kpfu.itis.kasimov.models.UserProgress;
import kpfu.itis.kasimov.repositories.UserProgressRepository;
import kpfu.itis.kasimov.dto.UserProgressDTO;

import java.sql.Timestamp;
import java.util.Optional;

@Service
@Transactional
public class UserProgressService {
    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Autowired
    public UserProgressService(UserProgressRepository userProgressRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.userProgressRepository = userProgressRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public void updateProgress(Integer userId, Integer courseId, int completedLessons, int totalLessons) {

        Optional<UserProgress> optionalProgress = userProgressRepository.findByUser_IdAndCourse_Id(userId, courseId);

        UserProgress progress;

        if (optionalProgress.isPresent()) {
            // Update existing progress
            progress = optionalProgress.get();
            progress.setCompletedLessons(completedLessons);
            progress.setTotalLessons(totalLessons);
            progress.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        } else {
            // Insert new progress
            User user = userRepository.findById(userId).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();

            progress = new UserProgress();
            progress.setUser(user);
            progress.setCourse(course);
            progress.setCompletedLessons(completedLessons);
            progress.setTotalLessons(totalLessons);
            progress.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        }

        userProgressRepository.save(progress);
    }


    public Optional<UserProgress> getEntityByUserAndCourse(Integer userId, Integer courseId) {
        return findByUserIdAndCourseId(userId, courseId)
                .map(dto -> UserProgressDTO.toEntity(dto, userRepository, courseRepository)); // репозитории можно внедрить в сервис
    }

    public Optional<UserProgressDTO> findByUserIdAndCourseId(Integer userId, Integer courseId) {
        return userProgressRepository.findByUser_IdAndCourse_Id(userId, courseId)
                .map(UserProgressDTO::valueOf);
    }
}
